package com.nearpick.app.domain.purchase.service.createStrategy

import com.nearpick.app.common.exception.InvalidPurchaseException
import com.nearpick.app.common.exception.KafkaPublishFailedException
import com.nearpick.app.common.exception.ProductOutOfStockException
import com.nearpick.app.domain.product.service.Product
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.event.PurchaseCreatedEvent
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.stock.service.StockService
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class FirstComePurchaseStrategy(
    private val stockService: StockService,
    private val kafkaTemplate: KafkaTemplate<String, PurchaseCreatedEvent>
) : PurchaseStrategy {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun createPurchase(request: CreatePurchaseRequest, userId: String): OrderReceivedResponse {
        val product = stockService.getProductCache(request.productId)

        if (!Product.isActiveFirstCome(product.status, product.startDt, product.endDt, stockService.getStock(request.productId))) {
            throw InvalidPurchaseException(request.productId)
        }

        val success = stockService.decreaseStockIfAvailable(request.productId, request.quantity)
        if (!success) {
            throw ProductOutOfStockException(request.productId)
        }

        try {
            val event = PurchaseCreatedEvent(
                userId = userId,
                failReason = null,
                request = request
            )
            kafkaTemplate.send("first-come-created", event).get(5, TimeUnit.SECONDS)
        } catch (e: Exception) {
            log.error("[FirstCome] Kafka 발행 실패, Redis 재고 복구 시작. productId={}, quantity={}", request.productId, request.quantity, e)
            stockService.recoverStock(request.productId, request.quantity)
            throw KafkaPublishFailedException(request.productId)
        }

        return Purchase.ofOrderReceived(request)
    }
}

