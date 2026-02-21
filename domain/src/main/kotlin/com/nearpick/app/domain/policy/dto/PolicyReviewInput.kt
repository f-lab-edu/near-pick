package com.nearpick.app.domain.policy.dto

data class PolicyReviewInput(
    val productId: String,
    val name: String,
    val description: String?,
    val category: String,
    val ocrText: String?,
    val imageHints: List<String>?
)
