package com.nearpick.app.domain.product.mapper

import com.nearpick.app.domain.brand.dto.BrandResponse
import com.nearpick.app.domain.product.dto.GetProductDetailResponse
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.service.Product
import com.nearpick.app.domain.user.dto.UserResponse
import org.springframework.stereotype.Component

@Component
class ProductResponseMapper {
    fun toResponse(product: Product): ProductResponse =
        ProductResponse(
            id = product.id,
            name = product.name,
            description = product.description,
            price = product.price,
            stock = product.stock,
            productType = product.productType,
            startDt = product.startDt,
            endDt = product.endDt,
            status = product.status
        )

    fun toDetailResponse(product: Product, seller: UserResponse, brand: BrandResponse): GetProductDetailResponse =
        GetProductDetailResponse(
            id = product.id,
            seller = seller,
            brand = brand,
            name = product.name,
            description = product.description,
            price = product.price,
            stock = product.stock,
            productType = product.productType,
            startDt = product.startDt,
            endDt = product.endDt,
            status = product.status
        )
}
