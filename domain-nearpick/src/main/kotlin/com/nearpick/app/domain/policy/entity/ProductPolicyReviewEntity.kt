package com.nearpick.app.domain.policy.entity

import com.nearpick.app.domain.policy.enum.PolicyVerdict
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "product_policy_review")
@EntityListeners(AuditingEntityListener::class)
class ProductPolicyReviewEntity(
    @Id
    @Column(name = "id", nullable = false, length = 255)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "product_id", nullable = false, length = 255)
    val productId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "verdict", nullable = false, length = 20)
    var verdict: PolicyVerdict,

    @Column(name = "confidence", nullable = false, precision = 4, scale = 3)
    var confidence: BigDecimal,

    @Column(name = "violations", columnDefinition = "JSON")
    var violations: String?,

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    var reason: String,

    @Column(name = "reviewed_by", nullable = false, length = 50)
    var reviewedBy: String = "AI",

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
)
