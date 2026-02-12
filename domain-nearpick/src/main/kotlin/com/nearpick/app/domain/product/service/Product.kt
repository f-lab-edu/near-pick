package com.nearpick.app.domain.product.service

import com.nearpick.app.common.exception.ProductStatusActiveException
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import java.math.BigInteger
import java.time.LocalDateTime

class Product(
    val id: String? = null,
    val sellerId: String,
    val brandId: String,
    var name: String,
    var description: String? = null,
    var price: BigInteger,
    var stock: Int? = null,
    var productType: ProductType,
    var startDt: LocalDateTime? = null,
    var endDt: LocalDateTime? = null,
    var status: ProductStatus? = ProductStatus.ACTIVE,

    ) {
    fun update(
        name: String?,
        description: String?,
        price: BigInteger?,
        stock: Int?,
        productType: ProductType?,
        startDt: LocalDateTime?,
        endDt: LocalDateTime?
    ) {
        if (this.status == ProductStatus.ACTIVE &&
            price != null && stock != null && productType != null && startDt != null && endDt != null
        ) {
            throw ProductStatusActiveException(this.id)
        }
        this.name = name ?: this.name
        this.description = description ?: this.description
        this.price = price ?: this.price
        this.stock = stock ?: this.stock
        this.productType = productType ?: this.productType
        this.startDt = startDt ?: this.startDt
        this.endDt = endDt ?: this.endDt
    }

    fun updateStatus(status: ProductStatus) {
        this.status = status
    }

    companion object {
        fun isActiveFirstCome(status: ProductStatus?, startDt: LocalDateTime?, endDt: LocalDateTime?, stock: Int?): Boolean {
            val allowedStatus = listOf(ProductStatus.ACTIVE, ProductStatus.PENDING)

            return allowedStatus.contains(status)
                && startDt?.isBefore(LocalDateTime.now()) == true
                && endDt?.isAfter(LocalDateTime.now()) == true
                && (stock ?: 0) > 0
        }
    }
}
