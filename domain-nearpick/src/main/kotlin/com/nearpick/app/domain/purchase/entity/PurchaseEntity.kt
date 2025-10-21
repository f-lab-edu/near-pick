package com.nearpick.app.domain.purchase.entity

import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import com.nearpick.app.domain.product.entity.ProductEntity
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
@Table(name = "purchase")
@EntityListeners(AuditingEntityListener::class)
class PurchaseEntity (
    @Id
    @Column(name = "id", nullable = false, length = 255)
    val id: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: ProductEntity,

    @Enumerated(EnumType.STRING)
    var productType: ProductType,

    var totalPrice: BigInteger,

    var quantity: Int,

    var reservationDt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var status: PurchaseStatus,

    var requestMessage: String? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @CreatedBy
    var createdBy: String? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    var updatedBy: String? = null
)
