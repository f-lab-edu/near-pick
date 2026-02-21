package com.nearpick.app.domain.policy.repository

import com.nearpick.app.domain.policy.entity.ProductPolicyReviewEntity
import com.nearpick.app.domain.policy.enum.PolicyVerdict
import org.springframework.data.jpa.repository.JpaRepository

interface ProductPolicyReviewRepository : JpaRepository<ProductPolicyReviewEntity, String> {
    fun findAllByVerdict(verdict: PolicyVerdict): List<ProductPolicyReviewEntity>
    fun findByProductId(productId: String): ProductPolicyReviewEntity?
}
