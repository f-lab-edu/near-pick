package com.nearpick.app.domain.purchase.event

import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest

data class PurchaseCreatedEvent(
    val userId: String,
    var failReason: String?,
    val request: CreatePurchaseRequest
)
