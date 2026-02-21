package com.nearpick.app.domain.policy.event

import com.nearpick.app.domain.policy.dto.PolicyReviewInput
import java.util.UUID

data class PolicyReviewRequestedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val productId: String,
    val name: String,
    val description: String?,
    val category: String,
    val ocrText: String?,
    val imageHints: List<String>?
) {
    fun toReviewInput() = PolicyReviewInput(
        productId = productId,
        name = name,
        description = description,
        category = category,
        ocrText = ocrText,
        imageHints = imageHints
    )
}
