package com.nearpick.domain.verification.entity

import com.nearpick.domain.verification.enum.VerificationStatus
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
@Table(name = "verification")
@EntityListeners(AuditingEntityListener::class)
class Verification(
    @Id
    @Column(nullable = false)
    val id: String, // UUID

    val type: String?, // SIGNUP_EMAIL, UPDATE_USER_EMAIL

    val userId: String? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val token: String,

    var status: String? = null, // PENDING, VERIFIED, EXPIRED, FAILED

    @Column(nullable = false)
    val validateDt: LocalDateTime,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @CreatedBy
    val createdBy: String? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    val updatedBy: String? = null

) {
    companion object {
        fun from(type: String, name: String, token: String, expirationMinutes: Long): Verification {
            return Verification(
                id = UUID.randomUUID().toString(),
                type = type,
                name = name,
                token = token,
                status = VerificationStatus.PENDING.name,
                validateDt = LocalDateTime.now().plusMinutes(expirationMinutes)
            )
        }
    }
}
