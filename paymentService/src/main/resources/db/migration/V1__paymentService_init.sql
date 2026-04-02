CREATE TABLE payments
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    created_at            datetime     NOT NULL,
    updated_at            datetime     NOT NULL,
    is_deleted            BIT(1) NULL,
    order_id              BIGINT       NOT NULL,
    amount                BIGINT       NOT NULL,
    status                VARCHAR(20)  NOT NULL,
    gateway_name          VARCHAR(50)  NOT NULL,
    external_order_id     VARCHAR(100) NULL,
    external_reference_id VARCHAR(100) NOT NULL,
    transaction_id        VARCHAR(100) NULL,
    payment_link_url      VARCHAR(500) NULL,
    idempotency_key       VARCHAR(100) NOT NULL,
    payment_method        VARCHAR(50) NULL,
    raw_webhook_data      TEXT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_idempotency_key UNIQUE (idempotency_key);

CREATE INDEX idx_external_ref ON payments (external_reference_id);

CREATE INDEX idx_order_id ON payments (order_id);