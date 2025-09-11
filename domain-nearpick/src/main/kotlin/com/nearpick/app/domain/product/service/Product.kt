package com.nearpick.app.domain.product.service

import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.brand.service.Brand
import com.nearpick.app.domain.product.dto.CreateProductRequest
import com.nearpick.app.domain.product.dto.GetProductDetailResponse
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.dto.UpdateProductRequest
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.user.entity.UserEntity
import com.nearpick.app.domain.user.service.User
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

class Product (
    val id: String? = null,
    val seller: UserEntity,
    val brandEntity: BrandEntity,
    var name: String,
    var description: String? = null,
    var price: BigInteger,
    var stock: Int? = null,
    var productType: ProductType,
    var reservationDeadline: LocalDateTime? = null,
    var isActive: Boolean? = true,

) {
    fun update(request: UpdateProductRequest) {
        this.name = request.name ?: this.name
        this.description = request.description ?: this.description
        this.price = request.price ?: this.price
        this.stock = request.stock ?: this.stock
        this.productType = request.productType ?: this.productType
        this.reservationDeadline = request.reservationDeadline ?: this.reservationDeadline
        this.isActive = request.isActive ?: this.isActive
    }

    fun toEntity(): ProductEntity {
        return ProductEntity(
            id = id?: UUID.randomUUID().toString(),
            seller = this.seller,
            brandEntity = this.brandEntity,
            name = this.name,
            description = this.description,
            price = this.price,
            stock = this.stock,
            productType = this.productType,
            reservationDeadline = this.reservationDeadline,
            isActive = this.isActive ?: true
        )
    }

    companion object {
        fun from(productEntity: ProductEntity): Product {
            return Product(
                id = productEntity.id,
                seller = productEntity.seller,
                brandEntity = productEntity.brandEntity,
                name = productEntity.name,
                description = productEntity.description,
                price = productEntity.price,
                stock = productEntity.stock,
                productType = productEntity.productType,
                reservationDeadline = productEntity.reservationDeadline,
                isActive = productEntity.isActive
            )
        }

        fun toResponse(productEntity: ProductEntity): ProductResponse =
            ProductResponse(
                id = productEntity.id,
                name = productEntity.name,
                description = productEntity.description,
                price = productEntity.price,
                stock = productEntity.stock,
                productType = productEntity.productType,
                reservationDeadline = productEntity.reservationDeadline,
                isActive = productEntity.isActive
            )

        fun toDetailResponse(productEntity: ProductEntity): GetProductDetailResponse =
            GetProductDetailResponse(
                id = productEntity.id,
                seller = User.toResponse(productEntity.seller),
                brand = Brand.toResponse(productEntity.brandEntity),
                name = productEntity.name,
                description = productEntity.description,
                price = productEntity.price,
                stock = productEntity.stock,
                productType = productEntity.productType,
                reservationDeadline = productEntity.reservationDeadline,
                isActive = productEntity.isActive
            )
    }
}
