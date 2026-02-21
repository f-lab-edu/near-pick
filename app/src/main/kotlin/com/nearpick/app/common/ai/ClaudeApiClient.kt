package com.nearpick.app.common.ai

import com.fasterxml.jackson.databind.ObjectMapper
import com.nearpick.app.domain.policy.dto.PolicyReviewInput
import com.nearpick.app.domain.policy.dto.PolicyReviewResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Component
class ClaudeApiClient(
    private val props: ClaudeApiProperties,
    private val webClientBuilder: WebClient.Builder,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val webClient: WebClient by lazy {
        webClientBuilder
            .baseUrl(props.baseUrl)
            .defaultHeader("x-api-key", props.apiKey)
            .defaultHeader("anthropic-version", "2023-06-01")
            .defaultHeader("content-type", "application/json")
            .build()
    }

    fun callPolicyReview(input: PolicyReviewInput): PolicyReviewResult {
        return runCatching {
            val response = webClient.post()
                .uri("/v1/messages")
                .bodyValue(buildRequestBody(input))
                .retrieve()
                .bodyToMono(ClaudeApiResponse::class.java)
                .timeout(Duration.ofSeconds(props.timeoutSeconds))
                .block()

            parseResult(response)
        }.getOrElse { e ->
            log.warn("[PolicyReview] Claude API 호출 실패, fail-safe 적용. reason={}", e.message)
            PolicyReviewResult.failSafe()
        }
    }

    private fun buildRequestBody(input: PolicyReviewInput): Map<String, Any> = mapOf(
        "model" to props.model,
        "max_tokens" to props.maxTokens,
        "system" to SYSTEM_PROMPT,
        "messages" to listOf(
            mapOf("role" to "user", "content" to buildUserMessage(input))
        )
    )

    private fun buildUserMessage(input: PolicyReviewInput): String = """
        상품명: ${input.name}
        설명: ${input.description ?: "(없음)"}
        카테고리: ${input.category}
        OCR 텍스트: ${input.ocrText ?: "(없음)"}
        이미지 힌트: ${input.imageHints?.joinToString(", ") ?: "(없음)"}
    """.trimIndent()

    private fun parseResult(response: ClaudeApiResponse?): PolicyReviewResult {
        val text = response?.content?.firstOrNull()?.text
            ?: return PolicyReviewResult.failSafe("빈 응답")
        return runCatching {
            objectMapper.readValue(text, PolicyReviewResult::class.java)
        }.getOrElse {
            log.warn("[PolicyReview] JSON 파싱 실패. raw={}", text)
            PolicyReviewResult.failSafe("JSON 파싱 실패")
        }
    }

    companion object {
        val SYSTEM_PROMPT = """
            너는 전자상거래 정책 심사관이다.
            제공된 "상품명/설명/카테고리/OCR 텍스트/이미지 힌트"만 사용해 판단한다.
            불확실하면 need_review를 반환한다.
            아래 JSON 스키마만 출력한다. 다른 텍스트는 절대 출력하지 않는다.

            {"verdict":"approved|rejected|need_review","confidence":0.0,"violations":[{"policy":"","detail":""}],"reason":""}

            판정 기준:
            - approved: 위반 없음 + confidence >= 0.8
            - rejected: 명백한 금지 항목 확인 + confidence >= 0.8
            - need_review: confidence < 0.5 이거나 판단이 불확실한 모든 경우

            금지 정책 목록 (policy 필드에 사용):
            - PROHIBITED_ITEM: 성인용품, 무허가 의약품, 총기, 마약류
            - FALSE_ADVERTISING: "100% 치료", "부작용 없음" 등 과장 표현
            - ILLEGAL_KEYWORD: 허가 없는 의학적 효능 주장
            - ADULT_CONTENT: 성적 묘사 이미지 힌트
            - COUNTERFEIT_RISK: 브랜드 위조·유사 상표 의심
        """.trimIndent()
    }
}

data class ClaudeApiResponse(
    val content: List<ClaudeContentBlock>
)

data class ClaudeContentBlock(
    val type: String,
    val text: String
)
