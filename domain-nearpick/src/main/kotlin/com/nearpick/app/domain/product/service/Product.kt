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
    var reservationDeadline: LocalDateTime? = null,
    var status: ProductStatus? = ProductStatus.ACTIVE,

    ) {
    fun update(
        name: String?,
        description: String?,
        price: BigInteger?,
        stock: Int?,
        productType: ProductType?,
        reservationDeadline: LocalDateTime?
    ) {
        if (this.status == ProductStatus.ACTIVE &&
            price != null && stock != null && productType != null && reservationDeadline != null
        ) {
            throw ProductStatusActiveException(this.id)
        }
        this.name = name ?: this.name
        this.description = description ?: this.description
        this.price = price ?: this.price
        this.stock = stock ?: this.stock
        this.productType = productType ?: this.productType
        this.reservationDeadline = reservationDeadline ?: this.reservationDeadline
    }

    fun updateStatus(status: ProductStatus) {
        this.status = status
    }
}
