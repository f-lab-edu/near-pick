package com.nearpick.app.domain.purchase.service.createStrategy

import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.OrderReceivedResponse

interface PurchaseStrategy {
    fun createPurchase(request: CreatePurchaseRequest, userId: String): OrderReceivedResponse
}
