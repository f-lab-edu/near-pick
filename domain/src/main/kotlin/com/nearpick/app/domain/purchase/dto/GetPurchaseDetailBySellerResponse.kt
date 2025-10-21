package com.nearpick.app.domain.purchase.dto

import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.user.dto.UserResponse
import java.math.BigInteger
import java.time.LocalDateTime

data class GetPurchaseDetailBySellerResponse (
    val id: String,
    val user: UserResponse,
    val product: ProductResponse,
    val totalPrice: BigInteger,
    val quantity: Int,
    val reservationDt: LocalDateTime? = null,
    val status: PurchaseStatus,
    val requestMessage: String? = null
)
