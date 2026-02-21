# Archive Index — 2026-02

| Feature | Match Rate | 완료일 | 경로 |
|---------|:----------:|--------|------|
| product-policy-reviewer | 91.3% | 2026-02-21 | `product-policy-reviewer/` |

## product-policy-reviewer

| 문서 | 파일 |
|------|------|
| Plan | `product-policy-reviewer/product-policy-reviewer.plan.md` |
| Design | `product-policy-reviewer/product-policy-reviewer.design.md` |
| Analysis | `product-policy-reviewer/product-policy-reviewer.analysis.md` |
| Report | `product-policy-reviewer/product-policy-reviewer.report.md` |

**요약**: NearPick 상품 등록 시 Claude API(claude-sonnet-4-6) 기반 자동 정책 심사 기능.
비동기 Kafka 이벤트, 4단계 Fail-Safe, `approved/rejected/need_review` JSON 판정 반환.
