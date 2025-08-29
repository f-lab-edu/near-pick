package com.nearpick.app.domain.product.entity

import com.nearpick.app.domain.brand.entity.Brand
import com.nearpick.app.domain.product.dto.CreateProductRequest
import com.nearpick.app.domain.product.dto.GetProductDetailResponse
import com.nearpick.app.domain.product.dto.ProductResponse
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "Product")
@EntityListeners(AuditingEntityListener::class)
class Product(
    @Id
    @Column(name = "id", nullable = false, length = 255)
    val id: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    val seller: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    val brand: Brand,

    var name: String,

    var description: String? = null,

    var price: BigInteger,

    var stock: Int? = null,

    @Enumerated(EnumType.STRING)
    var productType: ProductType,

    var reservationDeadline: LocalDateTime? = null,

    var isActive: Boolean? = true,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @CreatedBy
    var createdBy: String? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    var updatedBy: String? = null
) {
    companion object {
        fun createBySeller(request: CreateProductRequest, user: User, brand: Brand): Product =
            Product(
                id = UUID.randomUUID().toString(),
                seller = user,
                brand = brand,
                name = request.name,
                description = request.description,
                price = request.price,
                stock = request.stock,
                productType = request.productType,
                reservationDeadline = request.reservationDeadline,
                isActive = request.isActive ?: true
            )

        fun toResponse(product: Product): ProductResponse =
            ProductResponse(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                stock = product.stock,
                productType = product.productType,
                reservationDeadline = product.reservationDeadline,
                isActive = product.isActive
            )

        fun toDetailResponse(product: Product): GetProductDetailResponse =
            GetProductDetailResponse(
                id = product.id,
                seller = User.toResponse(product.seller),
                brand = Brand.toResponse(product.brand),
                name = product.name,
                description = product.description,
                price = product.price,
                stock = product.stock,
                productType = product.productType,
                reservationDeadline = product.reservationDeadline,
                isActive = product.isActive
            )
    }
}
