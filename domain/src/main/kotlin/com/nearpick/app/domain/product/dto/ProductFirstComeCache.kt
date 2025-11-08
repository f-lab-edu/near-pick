package com.nearpick.app.domain.product.dto

import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import java.math.BigInteger
import java.time.LocalDateTime

data class ProductFirstComeCache (
    val price: BigInteger,
    val productType: ProductType,
    val startDt: LocalDateTime? = null,
    val endDt: LocalDateTime? = null,
    val status: ProductStatus
)
