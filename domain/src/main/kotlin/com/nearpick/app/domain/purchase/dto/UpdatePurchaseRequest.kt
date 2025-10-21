package com.nearpick.app.domain.purchase.dto

import java.math.BigInteger
import java.time.LocalDateTime

data class UpdatePurchaseRequest (
    val price: BigInteger,
    val quantity: Int,
    val reservationDt: LocalDateTime? = null,
    val requestMessage: String? = null
)
