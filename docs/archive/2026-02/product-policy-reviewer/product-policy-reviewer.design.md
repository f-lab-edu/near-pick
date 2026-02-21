# Design: product-policy-reviewer

## 기본 정보

| 항목 | 내용 |
|------|------|
| Feature ID | product-policy-reviewer |
| 작성일 | 2026-02-21 |
| Plan 문서 | `docs/01-plan/features/product-policy-reviewer.plan.md` |
| Phase | Design |
| 기술 스택 | Kotlin, Spring Boot 3.5.3, Kafka, WebFlux WebClient, MySQL, JPA |

---

## 1. 전체 아키텍처

```
[판매자] POST /products
    │
    ▼
ProductServiceImpl.createProduct()
    │  status = PENDING
    │
    ├─ KafkaTemplate.send("product-policy-review", PolicyReviewRequestedEvent)
    │
    ▼
PolicyReviewEventConsumer   (@KafkaListener)
    │
    ▼
PolicyReviewerService.review(input)
    │
    ├─ Claude API 호출 (WebFlux WebClient, 비동기)
    │      System Prompt + User Message → JSON 응답
    │
    ├─ [approved]    → ProductStatus = ACTIVE
    ├─ [rejected]    → ProductStatus = INACTIVE_HIDDEN
    ├─ [need_review] → AdminReviewQueue 적재 (ProductStatus = PENDING 유지)
    │
    ▼
ProductPolicyReviewEntity 저장 (product_policy_review 테이블)
```

---

## 2. 멀티모듈 파일 배치

```
domain/
  src/main/kotlin/com/nearpick/app/domain/policy/
    dto/
      PolicyReviewInput.kt          # 심사 입력 DTO
      PolicyReviewResult.kt         # 심사 결과 DTO (JSON 스키마)
      PolicyViolation.kt            # violations 항목
    enum/
      PolicyVerdict.kt              # APPROVED / REJECTED / NEED_REVIEW
      PolicyViolationType.kt        # PROHIBITED_ITEM / FALSE_ADVERTISING / ...
    service/
      PolicyReviewerService.kt      # 인터페이스

domain-nearpick/
  src/main/kotlin/com/nearpick/app/domain/policy/
    entity/
      ProductPolicyReviewEntity.kt  # JPA 엔티티
    repository/
      ProductPolicyReviewRepository.kt
    service/
      PolicyReviewerServiceImpl.kt  # Claude API 호출 구현체
    event/
      PolicyReviewRequestedEvent.kt # Kafka 이벤트
      PolicyReviewEventConsumer.kt  # @KafkaListener

app/
  src/main/kotlin/com/nearpick/app/
    common/
      ai/
        ClaudeApiClient.kt          # WebFlux WebClient 래퍼
        ClaudeApiProperties.kt      # @ConfigurationProperties (API Key, URL, Model)
    domain/
      policy/
        controller/
          AdminPolicyReviewController.kt  # 관리자 need_review 목록/수동 판정 API
```

---

## 3. 도메인 클래스 상세 설계

### 3-1. PolicyVerdict (enum)

```kotlin
// domain/src/main/kotlin/com/nearpick/app/domain/policy/enum/PolicyVerdict.kt
enum class PolicyVerdict {
    APPROVED, REJECTED, NEED_REVIEW
}
```

### 3-2. PolicyViolationType (enum)

```kotlin
// domain/src/main/kotlin/com/nearpick/app/domain/policy/enum/PolicyViolationType.kt
enum class PolicyViolationType {
    PROHIBITED_ITEM,      // 금지 품목
    FALSE_ADVERTISING,    // 허위/과장 광고
    ILLEGAL_KEYWORD,      // 무허가 의학적 효능 주장
    ADULT_CONTENT,        // 성인 콘텐츠
    COUNTERFEIT_RISK      // 위조/유사 상표 위험
}
```

### 3-3. PolicyReviewInput (DTO)

```kotlin
// domain/src/main/kotlin/com/nearpick/app/domain/policy/dto/PolicyReviewInput.kt
data class PolicyReviewInput(
    val productId: String,
    val name: String,
    val description: String?,
    val category: String,
    val ocrText: String?,
    val imageHints: List<String>?
)
```

### 3-4. PolicyViolation (DTO)

```kotlin
// domain/src/main/kotlin/com/nearpick/app/domain/policy/dto/PolicyViolation.kt
data class PolicyViolation(
    val policy: String,   // PolicyViolationType 이름 또는 커스텀 정책명
    val detail: String
)
```

### 3-5. PolicyReviewResult (DTO — Claude 응답 JSON 스키마)

```kotlin
// domain/src/main/kotlin/com/nearpick/app/domain/policy/dto/PolicyReviewResult.kt
data class PolicyReviewResult(
    val verdict: PolicyVerdict,
    val confidence: Double,            // 0.0 ~ 1.0
    val violations: List<PolicyViolation>,
    val reason: String
) {
    companion object {
        /** AI 호출 실패 시 기본 fail-safe 결과 */
        fun failSafe(reason: String = "AI 심사 중 오류 발생, 관리자 검토 필요") = PolicyReviewResult(
            verdict = PolicyVerdict.NEED_REVIEW,
            confidence = 0.0,
            violations = emptyList(),
            reason = reason
        )
    }
}
```

### 3-6. PolicyReviewerService (인터페이스)

```kotlin
// domain/src/main/kotlin/com/nearpick/app/domain/policy/service/PolicyReviewerService.kt
interface PolicyReviewerService {
    fun review(input: PolicyReviewInput): PolicyReviewResult
}
```

---

## 4. Kafka 이벤트 설계

### 4-1. PolicyReviewRequestedEvent

```kotlin
// domain-nearpick/.../policy/event/PolicyReviewRequestedEvent.kt
data class PolicyReviewRequestedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val productId: String,
    val name: String,
    val description: String?,
    val category: String,
    val ocrText: String?,
    val imageHints: List<String>?
)
```

**Topic**: `product-policy-review`
**GroupId**: `nearpick-group` (기존 그룹 재사용)

### 4-2. PolicyReviewEventConsumer

```kotlin
// domain-nearpick/.../policy/event/PolicyReviewEventConsumer.kt
@Component
open class PolicyReviewEventConsumer(
    private val policyReviewerService: PolicyReviewerService,
    private val productRepository: ProductRepository,
    private val productPolicyReviewRepository: ProductPolicyReviewRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val CONTAINER_ID = "policyReviewConsumer"
    }

    @KafkaListener(
        id = CONTAINER_ID,
        topics = ["product-policy-review"],
        groupId = "nearpick-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consume(event: PolicyReviewRequestedEvent, ack: Acknowledgment) {
        val input = event.toReviewInput()
        val result = policyReviewerService.review(input)   // fail-safe 내장

        applyResult(event.productId, result)
        saveReviewRecord(event.productId, result)

        ack.acknowledge()
        log.info("[PolicyReview] 처리완료 productId={} verdict={}", event.productId, result.verdict)
    }

    private fun applyResult(productId: String, result: PolicyReviewResult) {
        val newStatus = when (result.verdict) {
            PolicyVerdict.APPROVED    -> ProductStatus.ACTIVE
            PolicyVerdict.REJECTED    -> ProductStatus.INACTIVE_HIDDEN
            PolicyVerdict.NEED_REVIEW -> ProductStatus.PENDING  // 상태 유지, 관리자 큐 적재
        }
        productRepository.updateStatusById(productId, newStatus)
    }
}
```

---

## 5. Claude API 클라이언트 설계

### 5-1. ClaudeApiProperties

```kotlin
// app/.../common/ai/ClaudeApiProperties.kt
@ConfigurationProperties(prefix = "claude.api")
data class ClaudeApiProperties(
    val apiKey: String,
    val baseUrl: String = "https://api.anthropic.com",
    val model: String = "claude-sonnet-4-6",
    val maxTokens: Int = 256,
    val timeoutSeconds: Long = 10
)
```

**application.yml 추가 설정**:
```yaml
claude:
  api:
    api-key: ${CLAUDE_API_KEY}
    model: claude-sonnet-4-6
    max-tokens: 256
    timeout-seconds: 10
```

### 5-2. ClaudeApiClient

```kotlin
// app/.../common/ai/ClaudeApiClient.kt
@Component
class ClaudeApiClient(
    private val props: ClaudeApiProperties,
    private val objectMapper: ObjectMapper
) {
    private val webClient = WebClient.builder()
        .baseUrl(props.baseUrl)
        .defaultHeader("x-api-key", props.apiKey)
        .defaultHeader("anthropic-version", "2023-06-01")
        .defaultHeader("content-type", "application/json")
        .build()

    /**
     * Claude API 호출 → PolicyReviewResult 반환
     * 모든 예외는 fail-safe (NEED_REVIEW) 로 변환
     */
    fun callPolicyReview(input: PolicyReviewInput): PolicyReviewResult {
        return try {
            val response = webClient.post()
                .uri("/v1/messages")
                .bodyValue(buildRequestBody(input))
                .retrieve()
                .bodyToMono(ClaudeApiResponse::class.java)
                .timeout(Duration.ofSeconds(props.timeoutSeconds))
                .block()

            parseResult(response)
        } catch (e: Exception) {
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
        return objectMapper.readValue(text, PolicyReviewResult::class.java)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ClaudeApiClient::class.java)

        val SYSTEM_PROMPT = """
            너는 전자상거래 정책 심사관이다.
            제공된 "상품명/설명/카테고리/OCR 텍스트/이미지 힌트"만 사용해 판단한다.
            불확실하면 need_review를 반환한다.
            아래 JSON 스키마만 출력한다. 다른 텍스트는 절대 출력하지 않는다.

            {
              "verdict": "approved" | "rejected" | "need_review",
              "confidence": <0.0~1.0 사이 숫자>,
              "violations": [
                { "policy": "<정책명>", "detail": "<위반 내용>" }
              ],
              "reason": "<판정 사유 1~2문장>"
            }

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
```

### 5-3. Claude API 응답 파싱용 내부 DTO

```kotlin
data class ClaudeApiResponse(
    val content: List<ClaudeContentBlock>
)

data class ClaudeContentBlock(
    val type: String,
    val text: String
)
```

---

## 6. 서비스 구현체 설계

```kotlin
// domain-nearpick/.../policy/service/PolicyReviewerServiceImpl.kt
@Service
class PolicyReviewerServiceImpl(
    private val claudeApiClient: ClaudeApiClient
) : PolicyReviewerService {

    override fun review(input: PolicyReviewInput): PolicyReviewResult {
        return claudeApiClient.callPolicyReview(input)
        // fail-safe는 ClaudeApiClient 내부에서 처리
    }
}
```

---

## 7. DB 스키마 (MySQL)

```sql
CREATE TABLE product_policy_review (
    id          VARCHAR(255)   NOT NULL PRIMARY KEY,
    product_id  VARCHAR(255)   NOT NULL,
    verdict     VARCHAR(20)    NOT NULL COMMENT 'APPROVED | REJECTED | NEED_REVIEW',
    confidence  DECIMAL(4, 3)  NOT NULL DEFAULT 0.000,
    violations  JSON           NULL     COMMENT '[{"policy":"...","detail":"..."}]',
    reason      TEXT           NOT NULL,
    reviewed_by VARCHAR(50)    NOT NULL DEFAULT 'AI' COMMENT 'AI 또는 관리자 userId',
    created_at  DATETIME(6)    NOT NULL,

    INDEX idx_product_id (product_id),
    INDEX idx_verdict     (verdict)
);
```

### JPA Entity

```kotlin
// domain-nearpick/.../policy/entity/ProductPolicyReviewEntity.kt
@Entity
@Table(name = "product_policy_review")
@EntityListeners(AuditingEntityListener::class)
class ProductPolicyReviewEntity(
    @Id
    @Column(name = "id", nullable = false, length = 255)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "product_id", nullable = false, length = 255)
    val productId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "verdict", nullable = false, length = 20)
    val verdict: PolicyVerdict,

    @Column(name = "confidence", nullable = false, precision = 4, scale = 3)
    val confidence: BigDecimal,

    @Column(name = "violations", columnDefinition = "JSON")
    val violations: String?,     // JSON 직렬화 문자열

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    val reason: String,

    @Column(name = "reviewed_by", nullable = false, length = 50)
    val reviewedBy: String = "AI",

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null
)
```

---

## 8. 관리자 API 설계

### 엔드포인트

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/admin/policy-reviews?verdict=NEED_REVIEW` | 검토 대기 목록 |
| POST | `/admin/policy-reviews/{reviewId}/decision` | 수동 판정 |

### 수동 판정 요청 Body

```json
{
  "verdict": "APPROVED | REJECTED",
  "reason": "관리자 검토 후 승인"
}
```

### 수동 판정 처리 흐름

```
관리자 POST /admin/policy-reviews/{reviewId}/decision
    ↓
verdict + reviewedBy(관리자 userId) 로 기존 레코드 업데이트
    ↓
ProductStatus 업데이트 (ACTIVE 또는 INACTIVE_HIDDEN)
```

---

## 9. ProductServiceImpl 수정 포인트

`createProduct()` 메서드에 Kafka 발행 추가:

```kotlin
// 기존 코드에 추가
override fun createProduct(request: CreateProductRequest, userId: String): ProductResponse {
    // ... 기존 로직 ...
    val entity = productMapper.toEntity(product)
    productRepository.save(entity)

    // [추가] 정책 심사 이벤트 발행
    val reviewEvent = PolicyReviewRequestedEvent(
        productId = entity.id,
        name = request.name,
        description = request.description,
        category = request.brandCategory,  // 브랜드 카테고리 참조
        ocrText = request.ocrText,
        imageHints = request.imageHints
    )
    kafkaTemplate.send("product-policy-review", reviewEvent)

    return productResponseMapper.toResponse(product)
}
```

> **주의**: `CreateProductRequest`에 `ocrText`, `imageHints` 필드 추가 필요

---

## 10. CreateProductRequest 확장

```kotlin
data class CreateProductRequest(
    val brandId: String,
    val name: String,
    val description: String? = null,
    val price: BigInteger,
    val stock: Int? = null,
    val productType: ProductType,
    val startDt: LocalDateTime? = null,
    val endDt: LocalDateTime? = null,
    val isActive: Boolean? = true,
    // [추가] 정책 심사용 필드
    val ocrText: String? = null,
    val imageHints: List<String>? = null
)
```

---

## 11. Fail-Safe 전략 요약

| 실패 상황 | 처리 |
|-----------|------|
| Claude API 타임아웃 (10초) | `PolicyReviewResult.failSafe()` → NEED_REVIEW |
| Claude API HTTP 오류 (4xx/5xx) | `PolicyReviewResult.failSafe()` → NEED_REVIEW |
| JSON 파싱 실패 | `PolicyReviewResult.failSafe()` → NEED_REVIEW |
| Kafka 발행 실패 | 로그 경고 + 상품 PENDING 유지 (기존 흐름과 동일) |
| confidence < 0.5 (Claude 반환) | 시스템 프롬프트 기준으로 Claude가 자체 NEED_REVIEW 반환 |

---

## 12. 의존성 추가

`app/build.gradle.kts` — 이미 포함됨 (확인 완료):
- `spring-boot-starter-webflux` (WebClient용)
- `spring-kafka` (Kafka 발행)

신규 추가 없음. **Claude API Key는 환경변수 `CLAUDE_API_KEY`로 주입**.

---

## 13. 구현 순서 (Do Phase 가이드)

1. **domain 모듈**: enum, DTO, 인터페이스 생성
2. **app 모듈**: `ClaudeApiProperties`, `ClaudeApiClient` 구현
3. **domain-nearpick**: `ProductPolicyReviewEntity`, Repository 생성
4. **domain-nearpick**: `PolicyReviewerServiceImpl` 구현 (ClaudeApiClient 주입)
5. **domain-nearpick**: `PolicyReviewRequestedEvent`, `PolicyReviewEventConsumer` 구현
6. **domain-nearpick**: `ProductServiceImpl.createProduct()` 수정 (이벤트 발행)
7. **domain**: `CreateProductRequest` 에 ocrText/imageHints 추가
8. **app**: `AdminPolicyReviewController` 구현
9. **DB**: `product_policy_review` 테이블 DDL 적용
10. **검증**: 상품 등록 → Kafka 이벤트 → Claude API 호출 → DB 저장 E2E 확인

---

## 14. 완료 기준 (Design DoD)

- [ ] 5가지 입력 필드가 Kafka 이벤트와 Claude API 호출에 모두 전달된다
- [ ] 시스템 프롬프트가 `SYSTEM_PROMPT` 상수로 관리된다
- [ ] Claude API 호출 실패 시 `PolicyReviewResult.failSafe()` 가 반환된다
- [ ] `product_policy_review` 테이블 DDL이 정의되었다
- [ ] 관리자 수동 판정 API 엔드포인트가 설계되었다
- [ ] 모든 클래스가 올바른 멀티모듈 위치에 배치되었다
