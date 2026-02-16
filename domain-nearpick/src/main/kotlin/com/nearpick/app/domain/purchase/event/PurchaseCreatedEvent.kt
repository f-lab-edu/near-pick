package com.nearpick.app.domain.purchase.event

import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import java.util.UUID

data class PurchaseCreatedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val userId: String,
    var failReason: String?,
    val request: CreatePurchaseRequest
)
