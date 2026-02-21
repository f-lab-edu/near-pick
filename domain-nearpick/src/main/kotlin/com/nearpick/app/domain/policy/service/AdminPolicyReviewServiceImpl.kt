package com.nearpick.app.domain.policy.service

import com.nearpick.app.domain.policy.dto.AdminPolicyDecisionRequest
import com.nearpick.app.domain.policy.dto.PolicyReviewSummary
import com.nearpick.app.domain.policy.enum.PolicyVerdict
import com.nearpick.app.domain.policy.repository.ProductPolicyReviewRepository
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminPolicyReviewServiceImpl(
    private val productPolicyReviewRepository: ProductPolicyReviewRepository,
    private val productRepository: ProductRepository
) : AdminPolicyReviewService {

    @Transactional(readOnly = true)
    override fun listReviews(verdict: PolicyVerdict): List<PolicyReviewSummary> {
        return productPolicyReviewRepository.findAllByVerdict(verdict).map { entity ->
            PolicyReviewSummary(
                id = entity.id,
                productId = entity.productId,
                verdict = entity.verdict,
                confidence = entity.confidence,
                violations = entity.violations,
                reason = entity.reason,
                reviewedBy = entity.reviewedBy,
                createdAt = entity.createdAt
            )
        }
    }

    @Transactional
    override fun decide(reviewId: String, request: AdminPolicyDecisionRequest, adminUserId: String) {
        val review = productPolicyReviewRepository.findById(reviewId)
            .orElseThrow { IllegalArgumentException("심사 결과를 찾을 수 없습니다. reviewId=$reviewId") }

        review.verdict = request.verdict
        review.reason = request.reason
        review.reviewedBy = adminUserId
        productPolicyReviewRepository.save(review)

        val newStatus = when (request.verdict) {
            PolicyVerdict.APPROVED    -> ProductStatus.ACTIVE
            PolicyVerdict.REJECTED    -> ProductStatus.INACTIVE_HIDDEN
            PolicyVerdict.NEED_REVIEW -> ProductStatus.PENDING
        }
        productRepository.updateStatusById(review.productId, newStatus)
    }
}
