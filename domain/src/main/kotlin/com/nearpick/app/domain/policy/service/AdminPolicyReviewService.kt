package com.nearpick.app.domain.policy.service

import com.nearpick.app.domain.policy.dto.AdminPolicyDecisionRequest
import com.nearpick.app.domain.policy.dto.PolicyReviewSummary
import com.nearpick.app.domain.policy.enum.PolicyVerdict

interface AdminPolicyReviewService {
    fun listReviews(verdict: PolicyVerdict): List<PolicyReviewSummary>
    fun decide(reviewId: String, request: AdminPolicyDecisionRequest, adminUserId: String)
}
