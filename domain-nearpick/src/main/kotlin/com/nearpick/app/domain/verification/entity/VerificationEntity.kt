package com.nearpick.app.domain.verification.entity

import com.nearpick.app.domain.verification.enum.VerificationStatus
import com.nearpick.app.domain.verification.enum.VerificationType
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
import java.util.UUID

@Entity
@Table(name = "verification")
@EntityListeners(AuditingEntityListener::class)
class VerificationEntity(
    @Id
    @Column(nullable = false)
    val id: String,

    @Enumerated(EnumType.STRING)
    val type: VerificationType?,

    val userId: String? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val token: String,

    @Enumerated(EnumType.STRING)
    var status: VerificationStatus? = null,

    @Column(nullable = false)
    val validateDt: LocalDateTime,

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
        fun from(type: VerificationType, name: String, token: String, expirationMinutes: Long): VerificationEntity {
            return VerificationEntity(
                id = UUID.randomUUID().toString(),
                type = type,
                name = name,
                token = token,
                status = VerificationStatus.PENDING,
                validateDt = LocalDateTime.now().plusMinutes(expirationMinutes)
            )
        }
    }
}
