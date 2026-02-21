package com.nearpick.app.domain.policy.dto

import com.nearpick.app.domain.policy.enum.PolicyVerdict

data class AdminPolicyDecisionRequest(
    val verdict: PolicyVerdict,
    val reason: String
)
