package com.nearpick.app.domain.purchase.event

import com.nearpick.app.domain.purchase.service.PurchaseService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
open class PurchaseCreatedEventConsumer(
    private val purchaseService: PurchaseService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["first-come-created"],
        groupId = "nearpick-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consume(event: PurchaseCreatedEvent, ack: Acknowledgment) {
        log.info("[Consumer] 이벤트 수신. eventId={}, productId={}, userId={}",
            event.eventId, event.request.productId, event.userId)

        purchaseService.applyPurchase(event.eventId, event.request, event.userId)

        ack.acknowledge()
        log.info("[Consumer] 처리 완료, offset commit. eventId={}", event.eventId)
    }
}

