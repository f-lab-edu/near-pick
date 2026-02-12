package com.nearpick.app.domain.purchase.service.createStrategy

import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse
import com.nearpick.app.domain.purchase.service.Purchase
import com.nearpick.app.domain.stock.service.StockService
import org.springframework.stereotype.Service

@Service
class ReservationPurchaseStrategy(
    private val stockService: StockService,
) : PurchaseStrategy {

    override fun createPurchase(request: CreatePurchaseRequest, userId: String): OrderReceivedResponse {
        val product = stockService.getProductCache(request.productId)

        //TODO: 예약 기능 추가 예정

        return Purchase.ofOrderReceived(request)
    }
}

