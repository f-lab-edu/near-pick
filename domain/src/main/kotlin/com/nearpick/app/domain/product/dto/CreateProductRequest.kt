package com.nearpick.app.domain.product.dto

import com.nearpick.app.domain.product.enum.ProductType
import java.math.BigInteger
import java.time.LocalDateTime

data class CreateProductRequest(
    val brandId: String,
    val name: String,
    val description: String? = null,
    val price: BigInteger,
    val stock: Int? = null,
    val productType: ProductType,
    val startDt: LocalDateTime? = null,
    val endDt: LocalDateTime? = null,
    val isActive: Boolean? = true,
    val ocrText: String? = null,
    val imageHints: List<String>? = null
)
