package com.nearpick.app.domain.address.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "user_address")
@EntityListeners(AuditingEntityListener::class)
class UserAddressEntity(
    @Id
    val id: String,

    @Column(nullable = false)
    val userId: String,

    var name: String?,
    var receiverName: String?,
    var phoneNumber: String?,

    @Column(nullable = false)
    val fullAddress: String,
    var addressDetail: String?,

    val province: String?,
    val district: String?,
    val neighborhood: String?,
    val street: String?,
    val buildingNumber: String?,

    var isDefault: Boolean,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @CreatedBy
    var createdBy: String? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    var updatedBy: String? = null
)
