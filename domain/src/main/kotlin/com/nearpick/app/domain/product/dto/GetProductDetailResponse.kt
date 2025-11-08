package com.nearpick.app.domain.product.dto

import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.user.dto.UserResponse
import java.math.BigInteger
import java.time.LocalDateTime

data class GetProductDetailResponse(
    val id: String,
    val seller: UserResponse,
    val brand: BrandResponse,
    val name: String,
    val description: String? = null,
    val price: BigInteger,
    val stock: Int? = null,
    val productType: ProductType,
    val startDt: LocalDateTime? = null,
    val endDt: LocalDateTime? = null,
    val status: ProductStatus
)
