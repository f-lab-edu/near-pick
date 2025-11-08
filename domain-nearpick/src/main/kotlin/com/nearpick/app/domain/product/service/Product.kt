package com.nearpick.app.domain.product.service

import com.nearpick.app.common.exception.ProductStatusActiveException
import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.brand.service.Brand
import com.nearpick.app.domain.product.dto.GetProductDetailResponse
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.dto.UpdateProductRequest
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.user.entity.UserEntity
import com.nearpick.app.domain.user.service.User
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

class Product(
    val id: String? = null,
    val seller: UserEntity,
    val brandEntity: BrandEntity,
    var name: String,
    var description: String? = null,
    var price: BigInteger,
    var stock: Int? = null,
    var productType: ProductType,
    var startDt: LocalDateTime? = null,
    var endDt: LocalDateTime? = null,
    var status: ProductStatus? = ProductStatus.ACTIVE,

    ) {
    fun update(request: UpdateProductRequest) {
        if (this.status == ProductStatus.ACTIVE &&
            request.price != null && request.stock != null && request.productType != null && request.startDt != null && request.endDt != null
        ) {
            throw ProductStatusActiveException(this.id)
        }

        this.name = request.name ?: this.name
        this.description = request.description ?: this.description
        this.price = request.price ?: this.price
        this.stock = request.stock ?: this.stock
        this.productType = request.productType ?: this.productType
        this.startDt = request.startDt
        this.endDt = request.endDt ?: this.endDt
    }

    fun updateStatus(status: ProductStatus) {
        this.status = status
    }

    fun toEntity(): ProductEntity {
        return ProductEntity(
            id = this.id ?: UUID.randomUUID().toString(),
            seller = this.seller,
            brandEntity = this.brandEntity,
            name = this.name,
            description = this.description,
            price = this.price,
            stock = this.stock,
            productType = this.productType,
            startDt = this.startDt,
            endDt = this.endDt,
            status = this.status ?: ProductStatus.ACTIVE
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
                startDt = productEntity.startDt,
                endDt = productEntity.endDt,
                status = productEntity.status
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
                startDt = productEntity.startDt,
                endDt = productEntity.endDt,
                status = productEntity.status
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
                startDt = productEntity.startDt,
                endDt = productEntity.endDt,
                status = productEntity.status
            )

        fun isActiveFirstCome(productEntity: ProductEntity): Boolean {
            return  isActiveFirstCome(productEntity.status, productEntity.startDt, productEntity.endDt, productEntity.stock)
        }

        fun isActiveFirstCome(status: ProductStatus, startDt: LocalDateTime?, endDt: LocalDateTime?, stock: Int?): Boolean {
            val allowedStatus = listOf(ProductStatus.ACTIVE, ProductStatus.PENDING)

            return allowedStatus.contains(status)
                && startDt?.isBefore(LocalDateTime.now()) == true
                && endDt?.isAfter(LocalDateTime.now()) == true
                && (stock ?: 0) > 0
        }
    }
}
