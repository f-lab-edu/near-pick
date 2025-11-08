package com.nearpick.app.domain.purchase.event

import com.nearpick.app.domain.purchase.service.PurchaseService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
open class PurchaseCreatedEventConsumer(
    private val purchaseService: PurchaseService,
) {

    @KafkaListener(topics = ["first-come-created"], groupId = "nearpick-group")
    fun consume(event: PurchaseCreatedEvent) {

        val success = purchaseService.applyPurchase(event.request, event.userId)

        //TODO: 이후 실패 처리 정책 추가 예정
//        if (!success) {
//            kafkaTemplate.send("purchase.failed",event.request)
//        }
    }

}

