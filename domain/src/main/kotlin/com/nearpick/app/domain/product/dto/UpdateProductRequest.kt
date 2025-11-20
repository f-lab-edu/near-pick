package com.nearpick.app.domain.product.dto

import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import java.math.BigInteger
import java.time.LocalDateTime

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: BigInteger? = null,
    val stock: Int? = null,
    val productType: ProductType? = null,
    val reservationDeadline: LocalDateTime? = null
)
