package com.nearpick.app.domain.policy.dto

import com.nearpick.app.domain.policy.enum.PolicyVerdict
import java.math.BigDecimal
import java.time.LocalDateTime

data class PolicyReviewSummary(
    val id: String,
    val productId: String,
    val verdict: PolicyVerdict,
    val confidence: BigDecimal,
    val violations: String?,
    val reason: String,
    val reviewedBy: String,
    val createdAt: LocalDateTime?
)
