package com.nearpick.domain.address.entity

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
import java.util.*

@Entity
@Table(name = "UserAddress")
@EntityListeners(AuditingEntityListener::class)
class UserAddress(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    val userId: String,

    val name: String?,
    val receiverName: String?,
    val phoneNumber: String?,

    @Column(nullable = false)
    val fullAddress: String,
    val addressDetail: String?,

    val province: String?,
    val district: String?,
    val neighborhood: String?,
    val street: String?,
    val buildingNumber: String?,

    val isDefault: Boolean? = false,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @CreatedBy
    val createdBy: String? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    val updatedBy: String? = null
)
