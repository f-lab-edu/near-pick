package com.nearpick.app.domain.purchase.dto

import com.nearpick.app.domain.purchase.enum.PurchaseStatus

data class UpdatePurchaseStatusRequest (
    val status: PurchaseStatus
)
