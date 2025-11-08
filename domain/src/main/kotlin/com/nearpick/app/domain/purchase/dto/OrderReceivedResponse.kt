package com.nearpick.app.domain.purchase.dto

import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import java.math.BigInteger
import java.time.LocalDateTime

data class OrderReceivedResponse (
    val id: String? = null,
    val productId: String,
    val productType: ProductType,
    val totalPrice: BigInteger,
    val quantity: Int,
    val reservationDt: LocalDateTime? = null,
    val status: PurchaseStatus,
    val requestMessage: String? = null
)
