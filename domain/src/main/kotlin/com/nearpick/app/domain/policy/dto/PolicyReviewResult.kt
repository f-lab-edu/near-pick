package com.nearpick.app.domain.policy.dto

import com.nearpick.app.domain.policy.enum.PolicyVerdict

data class PolicyReviewResult(
    val verdict: PolicyVerdict,
    val confidence: Double,
    val violations: List<PolicyViolation>,
    val reason: String
) {
    companion object {
        fun failSafe(reason: String = "AI 심사 중 오류 발생, 관리자 검토 필요") = PolicyReviewResult(
            verdict = PolicyVerdict.NEED_REVIEW,
            confidence = 0.0,
            violations = emptyList(),
            reason = reason
        )
    }
}
