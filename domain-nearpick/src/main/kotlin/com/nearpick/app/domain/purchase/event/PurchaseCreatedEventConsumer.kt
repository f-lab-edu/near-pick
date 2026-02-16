package com.nearpick.app.domain.purchase.event

import com.nearpick.app.domain.purchase.protection.ConsumerProtectionState
import com.nearpick.app.domain.purchase.protection.ConsumerProtectionStateProvider
import com.nearpick.app.domain.purchase.service.PurchaseService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
open class PurchaseCreatedEventConsumer(
    private val purchaseService: PurchaseService,
    private val protectionStateProvider: ConsumerProtectionStateProvider
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val CONTAINER_ID = "purchaseConsumer"
        private const val THROTTLE_SLEEP_MS = 500L
    }

    @KafkaListener(
        id = CONTAINER_ID,
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

        if (protectionStateProvider.getState() == ConsumerProtectionState.THROTTLED) {
            Thread.sleep(THROTTLE_SLEEP_MS)
        }
    }
}
