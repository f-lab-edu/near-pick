package com.nearpick.app.common.ai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "claude.api")
data class ClaudeApiProperties(
    val apiKey: String,
    val baseUrl: String = "https://api.anthropic.com",
    val model: String = "claude-sonnet-4-6",
    val maxTokens: Int = 256,
    val timeoutSeconds: Long = 10
)
