package com.nearpick.domain.product.dto

import com.nearpick.domain.product.enum.ProductType
import java.math.BigInteger
import java.time.LocalDateTime

data class CreateProductRequest(
    val brandId: String,
    val name: String,
    val description: String? = null,
    val price: BigInteger,
    val stock: Int? = null,
    val productType: ProductType,
    val reservationDeadline: LocalDateTime? = null,
    val isActive: Boolean? = true
)
