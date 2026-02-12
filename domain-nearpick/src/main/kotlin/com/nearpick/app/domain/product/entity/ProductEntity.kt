package com.nearpick.app.domain.product.entity

import com.nearpick.app.domain.brand.entity.BrandEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.user.entity.UserEntity
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

@Entity
@Table(name = "product")
@EntityListeners(AuditingEntityListener::class)
class ProductEntity(
    @Id
    @Column(name = "id", nullable = false, length = 255)
    val id: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    val seller: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    val brandEntity: BrandEntity,

    var name: String,

    var description: String? = null,

    var price: BigInteger,

    var stock: Int? = null,

    @Enumerated(EnumType.STRING)
    var productType: ProductType,

    var startDt: LocalDateTime? = null,

    var endDt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var status: ProductStatus,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @CreatedBy
    var createdBy: String? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    var updatedBy: String? = null
)
