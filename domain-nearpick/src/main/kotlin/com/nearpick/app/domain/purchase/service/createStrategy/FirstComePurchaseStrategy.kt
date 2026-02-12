package com.nearpick.app.domain.purchase.service.createStrategy

import com.nearpick.app.common.exception.InvalidPurchaseException
import com.nearpick.app.common.exception.ProductOutOfStockException
import com.nearpick.app.domain.product.service.Product
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.event.PurchaseCreatedEvent
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.stock.service.StockService
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class FirstComePurchaseStrategy(
    private val stockService: StockService,
    private val kafkaTemplate: KafkaTemplate<String, PurchaseCreatedEvent>
) : PurchaseStrategy {

    override fun createPurchase(request: CreatePurchaseRequest, userId: String): OrderReceivedResponse {
        val product = stockService.getProductCache(request.productId)
        val stock = stockService.getStock(request.productId)

        if(stock < request.quantity) {
            throw ProductOutOfStockException(request.productId)
        } else if(!Product.isActiveFirstCome(product.status, product.startDt, product.endDt, stock)) {
            throw InvalidPurchaseException(request.productId)
        }

        val success = stockService.decreaseStockIfAvailable(request.productId, request.quantity)

        if (success) {
            val event = PurchaseCreatedEvent(userId, null, request)
            kafkaTemplate.send("first-come-created", event)

            return Purchase.ofOrderReceived(request)
        } else {
            throw ProductOutOfStockException(request.productId)
        }
    }
}

