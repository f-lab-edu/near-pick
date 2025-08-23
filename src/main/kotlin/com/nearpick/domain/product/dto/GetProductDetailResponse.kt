package com.nearpick.domain.product.dto

import com.nearpick.domain.brand.dto.BrandResponse
import com.nearpick.domain.product.enum.ProductType
import com.nearpick.domain.user.dto.UserResponse
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
    val reservationDeadline: LocalDateTime? = null,
    val isActive: Boolean? = true
)
