package com.nearpick.app.domain.purchase.dto

import com.nearpick.app.domain.product.enum.ProductType
import java.math.BigInteger
import java.time.LocalDateTime

data class CreatePurchaseRequest(
    val productId: String,
    val productType: ProductType,
    val price: BigInteger,
    val quantity: Int,
    val reservationDt: LocalDateTime? = null,
    val requestMessage: String? = null
)
