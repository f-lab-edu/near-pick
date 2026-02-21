-- product_policy_review 테이블 DDL
-- Feature: product-policy-reviewer
-- Created: 2026-02-21

CREATE TABLE IF NOT EXISTS product_policy_review (
    id          VARCHAR(255)   NOT NULL,
    product_id  VARCHAR(255)   NOT NULL,
    verdict     VARCHAR(20)    NOT NULL COMMENT 'APPROVED | REJECTED | NEED_REVIEW',
    confidence  DECIMAL(4, 3)  NOT NULL DEFAULT 0.000,
    violations  JSON           NULL     COMMENT '[{"policy":"...","detail":"..."}]',
    reason      TEXT           NOT NULL,
    reviewed_by VARCHAR(50)    NOT NULL DEFAULT 'AI' COMMENT 'AI 또는 관리자 userId',
    created_at  DATETIME(6)    NOT NULL,

    PRIMARY KEY (id),
    INDEX idx_product_policy_review_product_id (product_id),
    INDEX idx_product_policy_review_verdict    (verdict)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
