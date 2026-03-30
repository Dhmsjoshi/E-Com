CREATE TABLE orders
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6)  NOT NULL,
    is_deleted     BIT(1)       NOT NULL DEFAULT 0,
    user_id        BINARY(16)   NOT NULL,
    total_amount   DOUBLE       NOT NULL,
    order_status   VARCHAR(255) NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    -- Performance ke liye Index
    INDEX idx_user_id (user_id)
);

CREATE TABLE order_items
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    is_deleted   BIT(1)       NOT NULL DEFAULT 0,
    order_id     BIGINT       NOT NULL,
    product_id   BIGINT       NOT NULL,
    product_name VARCHAR(255) NULL,
    quantity     INT          NOT NULL,
    price        DOUBLE       NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    INDEX idx_order_id (order_id)
);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);