package com.nearpick.domain.user.entity

import com.nearpick.common.constant.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
@Table(name = "User")
@EntityListeners(AuditingEntityListener::class)
class User(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(unique = true, nullable = false)
    var nickname: String,

    @Column(nullable = false)
    var password: String,

    var profileImageUrl: String? = null,

    var phoneNumber: String? = null,

    var locationId: String? = null,

    var accountHolderName: String? = null,

    var bankName: String? = null,

    var accountNumber: String? = null,

    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER,

    var isActive: Boolean = true,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @CreatedBy
    var createdBy: String? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    var updatedBy: String? = null
)
