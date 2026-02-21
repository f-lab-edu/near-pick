# Plan: product-policy-reviewer

## 기본 정보

| 항목 | 내용 |
|------|------|
| Feature ID | product-policy-reviewer |
| 작성일 | 2026-02-21 |
| 작성자 | AI-Assisted |
| Phase | Plan |
| 우선순위 | High |
| 관련 도메인 | Product, Brand |

---

## 1. 배경 및 목적

NearPick은 판매자(소상공인·브랜드)가 자유롭게 상품을 등록할 수 있는 플랫폼이다.
상품 등록 시 허위·과장 광고, 금지 품목, 불법 표현이 포함될 수 있어 **플랫폼 신뢰도와 법적 리스크** 모두를 위협한다.

**목적**: 상품이 `PENDING` 상태로 등록된 직후, AI 정책 심사관이 자동으로 내용을 분석하여
승인(`approved`) / 거절(`rejected`) / 검토 필요(`need_review`)를 판정하고,
결과를 JSON으로 저장·반환한다.

---

## 2. 범위 (Scope)

### In-Scope
- 상품 등록 시 자동 정책 심사 트리거 (비동기)
- 입력 데이터: 상품명, 설명, 카테고리, OCR 텍스트, 이미지 힌트
- 판정 결과 JSON 반환 및 DB 저장
- `need_review` 시 관리자 수동 검토 큐에 적재
- `rejected` 시 ProductStatus → `INACTIVE_HIDDEN` 자동 전환

### Out-of-Scope
- 이미지 직접 분석 (이미지 힌트 텍스트 기반으로만 처리)
- 실시간 동기 심사 (등록 응답 속도 보호를 위해 비동기)
- 판매자 이의신청 UI (1차 MVP 제외)

---

## 3. 사용자 스토리

| ID | 역할 | 요구사항 | 우선순위 |
|----|------|----------|----------|
| US-01 | 판매자 | 상품 등록 후 심사 결과를 알 수 있다 | Must |
| US-02 | 관리자 | `need_review` 상품 목록을 조회하고 직접 판정할 수 있다 | Must |
| US-03 | 시스템 | 금지 표현이 포함된 상품을 자동으로 숨길 수 있다 | Must |
| US-04 | 시스템 | 불확실한 경우 섣불리 거절하지 않고 사람이 검토하게 한다 | Must |

---

## 4. 기능 요구사항

### FR-01: 정책 심사 입력
심사는 아래 5가지 필드만 사용한다. 외부 데이터 조회 금지.

| 필드 | 타입 | 설명 |
|------|------|------|
| `name` | String | 상품명 |
| `description` | String? | 상품 설명 |
| `category` | String | 카테고리 (예: FOOD, BEAUTY, HEALTH) |
| `ocrText` | String? | 상품 이미지에서 추출한 OCR 텍스트 |
| `imageHints` | List<String>? | 이미지 분류 힌트 태그 (예: ["alcohol", "adult"]) |

### FR-02: 판정 결과 JSON 스키마
심사관은 **반드시** 아래 스키마만 출력한다.

```json
{
  "verdict": "approved | rejected | need_review",
  "confidence": 0.0,
  "violations": [
    {
      "policy": "금지 정책 이름",
      "detail": "위반 상세 내용"
    }
  ],
  "reason": "심사 사유 요약 (1~2문장)"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `verdict` | enum | `approved` / `rejected` / `need_review` |
| `confidence` | Float (0~1) | 심사 확신도. 0.5 미만이면 need_review |
| `violations` | Array | 위반 항목 목록. 없으면 빈 배열 |
| `violations[].policy` | String | 위반한 정책명 |
| `violations[].detail` | String | 구체적 위반 내용 |
| `reason` | String | 판정 사유 요약 |

### FR-03: 판정 기준 (1차 MVP)

| 판정 | 조건 |
|------|------|
| `approved` | 모든 정책 위반 없음 + confidence >= 0.8 |
| `rejected` | 명백한 금지 품목·표현 확인 + confidence >= 0.8 |
| `need_review` | confidence < 0.5 OR 판단 불가 OR 금지어 부분 포함 |

**금지 정책 카테고리 (초기 세트)**:
- `PROHIBITED_ITEM`: 성인용품, 의약품 무허가 판매, 총기류 등
- `FALSE_ADVERTISING`: "100% 치료", "부작용 없음" 등 과장 표현
- `ILLEGAL_KEYWORD`: 허가 없이 의학적 효능 주장
- `ADULT_CONTENT`: 성적 묘사 포함 이미지 힌트
- `COUNTERFEIT_RISK`: 브랜드 위조·유사 상표 의심

### FR-04: 처리 흐름

```
판매자 상품 등록 (POST /products)
    ↓
ProductStatus = PENDING 저장
    ↓
PolicyReviewEvent 발행 (Kafka / Spring Event)
    ↓
PolicyReviewerService.review(input)
    ↓
AI 모델 호출 (Claude API)
    ↓
[approved]         → ProductStatus = ACTIVE
[rejected]         → ProductStatus = INACTIVE_HIDDEN + 판매자 알림
[need_review]      → AdminReviewQueue 적재 (관리자 수동 검토)
```

### FR-05: 안전 기본값 (Fail-Safe)
- AI 모델 호출 실패 시 → 자동으로 `need_review` 반환 (거절하지 않음)
- 타임아웃 (3초 초과) → `need_review` fallback
- JSON 파싱 실패 → `need_review` fallback

---

## 5. 비기능 요구사항

| 항목 | 요구사항 |
|------|----------|
| 응답 시간 | 비동기 처리 (상품 등록 API 응답 속도 영향 없음) |
| 정확도 목표 | approved/rejected 정확도 >= 90%, need_review 오분류 최소화 |
| 가용성 | AI 호출 실패 시 fail-safe로 need_review 반환 (서비스 중단 없음) |
| 확장성 | 정책 세트를 DB 또는 설정 파일로 관리 (코드 변경 없이 정책 추가 가능) |
| 감사 로그 | 모든 심사 결과를 `product_policy_review` 테이블에 저장 |

---

## 6. 시스템 의존성

| 의존성 | 용도 |
|--------|------|
| Claude API (claude-sonnet-4-6) | 정책 심사 판정 |
| Kafka / Spring ApplicationEvent | 비동기 이벤트 발행 |
| PostgreSQL | 심사 결과 저장 (`product_policy_review` 테이블) |
| 기존 `ProductEntity` | 상태 업데이트 (`ProductStatus`) |

---

## 7. 데이터 모델 (예비 설계)

### product_policy_review 테이블

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | VARCHAR(255) PK | UUID |
| `product_id` | VARCHAR(255) FK | 심사 대상 상품 |
| `verdict` | VARCHAR(20) | approved / rejected / need_review |
| `confidence` | DECIMAL(4,3) | 확신도 |
| `violations` | JSONB | 위반 항목 배열 |
| `reason` | TEXT | 판정 사유 |
| `reviewed_by` | VARCHAR(50) | `AI` 또는 관리자 ID |
| `created_at` | TIMESTAMP | 심사 시각 |

---

## 8. 예외 및 리스크

| 리스크 | 영향 | 대응 방안 |
|--------|------|-----------|
| AI 모델 과도한 거절 (False Positive) | 정상 상품 숨김 → 판매자 불만 | confidence 임계값 조정, need_review 우선 사용 |
| AI 할루시네이션 | 잘못된 위반 항목 생성 | violations는 입력 텍스트 기반 근거만 허용 |
| API 비용 급증 | 운영 비용 증가 | 심사 요청 Rate Limit + 캐싱 (동일 텍스트 해시 기준) |
| 판매자 이의신청 없음 (MVP) | 억울한 거절 처리 불가 | 모든 rejected는 관리자 재검토 가능 구조 유지 |

---

## 9. 구현 우선순위

| Phase | 내용 | 완료 기준 |
|-------|------|-----------|
| MVP | FR-01~05 전체 + fail-safe | 상품 등록 후 자동 심사 결과 DB 저장 |
| 확장 | 정책 세트 관리 UI (관리자) | 코드 변경 없이 정책 추가 가능 |
| 확장 | 판매자 이의신청 API | 이의신청 → need_review 재처리 |

---

## 10. 완료 기준 (Definition of Done)

- [ ] 상품 등록 시 자동으로 정책 심사 이벤트가 발행된다
- [ ] AI 심사관이 5가지 입력 필드만 사용해 판정한다
- [ ] 판정 결과가 FR-02 JSON 스키마를 준수한다
- [ ] `approved` → ACTIVE, `rejected` → INACTIVE_HIDDEN 상태 전환이 동작한다
- [ ] AI 호출 실패/타임아웃 시 `need_review`로 fallback된다
- [ ] 모든 심사 결과가 `product_policy_review` 테이블에 저장된다
- [ ] Gap Analysis Match Rate >= 90%
