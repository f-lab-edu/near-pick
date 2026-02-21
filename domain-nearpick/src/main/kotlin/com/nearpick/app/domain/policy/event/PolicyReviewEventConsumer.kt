package com.nearpick.app.domain.policy.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nearpick.app.domain.policy.dto.PolicyReviewResult
import com.nearpick.app.domain.policy.entity.ProductPolicyReviewEntity
import com.nearpick.app.domain.policy.enum.PolicyVerdict
import com.nearpick.app.domain.policy.repository.ProductPolicyReviewRepository
import com.nearpick.app.domain.policy.service.PolicyReviewerService
import com.nearpick.app.domain.product.entity.ProductEntity
import com.nearpick.app.domain.product.enum.ProductStatus
import com.nearpick.app.domain.product.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
open class PolicyReviewEventConsumer(
    private val policyReviewerService: PolicyReviewerService,
    private val productRepository: ProductRepository,
    private val productPolicyReviewRepository: ProductPolicyReviewRepository,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val CONTAINER_ID = "policyReviewConsumer"
    }

    @KafkaListener(
        id = CONTAINER_ID,
        topics = ["product-policy-review"],
        groupId = "nearpick-group",
        containerFactory = "policyReviewListenerContainerFactory"
    )
    @Transactional
    open fun consume(event: PolicyReviewRequestedEvent, ack: Acknowledgment) {
        log.info("[PolicyReview] 이벤트 수신. eventId={}, productId={}", event.eventId, event.productId)

        val result = policyReviewerService.review(event.toReviewInput())
        applyProductStatus(event.productId, result)
        saveReviewRecord(event.productId, result)

        ack.acknowledge()
        log.info("[PolicyReview] 처리 완료. productId={}, verdict={}, confidence={}",
            event.productId, result.verdict, result.confidence)
    }

    private fun applyProductStatus(productId: String, result: PolicyReviewResult) {
        val newStatus = when (result.verdict) {
            PolicyVerdict.APPROVED    -> ProductStatus.ACTIVE
            PolicyVerdict.REJECTED    -> ProductStatus.INACTIVE_HIDDEN
            PolicyVerdict.NEED_REVIEW -> ProductStatus.PENDING
        }
        productRepository.updateStatusById(productId, newStatus)
        log.info("[PolicyReview] 상품 상태 변경. productId={}, status={}", productId, newStatus)
    }

    private fun saveReviewRecord(productId: String, result: PolicyReviewResult) {
        val violationsJson = runCatching {
            objectMapper.writeValueAsString(result.violations)
        }.getOrElse { "[]" }

        val entity = ProductPolicyReviewEntity(
            productId = productId,
            verdict = result.verdict,
            confidence = BigDecimal.valueOf(result.confidence),
            violations = violationsJson,
            reason = result.reason,
            reviewedBy = "AI"
        )
        productPolicyReviewRepository.save(entity)
    }
}
