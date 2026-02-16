package com.nearpick.app.config

import com.nearpick.app.domain.purchase.event.PurchaseCreatedEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.RetryListener
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.util.backoff.FixedBackOff

@Configuration
open class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.consumer.group-id}") private val groupId: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    open fun purchaseConsumerFactory(): ConsumerFactory<String, PurchaseCreatedEvent> {
        val deserializer = JsonDeserializer(PurchaseCreatedEvent::class.java)
        deserializer.addTrustedPackages("com.nearpick.app.domain.purchase.event")
        deserializer.setUseTypeMapperForKey(false)

        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 10
        )

        return DefaultKafkaConsumerFactory(props, StringDeserializer(), deserializer)
    }

    @Bean
    open fun kafkaListenerContainerFactory(
        purchaseConsumerFactory: ConsumerFactory<String, PurchaseCreatedEvent>,
        kafkaTemplate: KafkaTemplate<String, PurchaseCreatedEvent>
    ): ConcurrentKafkaListenerContainerFactory<String, PurchaseCreatedEvent> {
        val recoverer = DeadLetterPublishingRecoverer(kafkaTemplate)
        val errorHandler = DefaultErrorHandler(recoverer, FixedBackOff(1000L, 3L))

        errorHandler.setRetryListeners(object : RetryListener {
            override fun failedDelivery(record: org.apache.kafka.clients.consumer.ConsumerRecord<*, *>, ex: Exception, deliveryAttempt: Int) {
                log.warn("[KafkaRetry] 재시도 {}/3, topic={}, offset={}, error={}",
                    deliveryAttempt, record.topic(), record.offset(), ex.message)
            }
        })

        val factory = ConcurrentKafkaListenerContainerFactory<String, PurchaseCreatedEvent>()
        factory.consumerFactory = purchaseConsumerFactory
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        factory.setCommonErrorHandler(errorHandler)
        return factory
    }
}
