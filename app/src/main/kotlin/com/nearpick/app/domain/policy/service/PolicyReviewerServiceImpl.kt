package com.nearpick.app.domain.policy.service

import com.nearpick.app.common.ai.ClaudeApiClient
import com.nearpick.app.domain.policy.dto.PolicyReviewInput
import com.nearpick.app.domain.policy.dto.PolicyReviewResult
import org.springframework.stereotype.Service

@Service
class PolicyReviewerServiceImpl(
    private val claudeApiClient: ClaudeApiClient
) : PolicyReviewerService {

    override fun review(input: PolicyReviewInput): PolicyReviewResult {
        return claudeApiClient.callPolicyReview(input)
    }
}
