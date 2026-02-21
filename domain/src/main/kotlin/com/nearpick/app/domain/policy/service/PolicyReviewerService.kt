package com.nearpick.app.domain.policy.service

import com.nearpick.app.domain.policy.dto.PolicyReviewInput
import com.nearpick.app.domain.policy.dto.PolicyReviewResult

interface PolicyReviewerService {
    fun review(input: PolicyReviewInput): PolicyReviewResult
}
