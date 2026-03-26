CREATE TABLE cart_items
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at   datetime NOT NULL,
    updated_at   datetime NULL,
    is_deleted   BIT(1)   NOT NULL,
    product_id   BIGINT   NOT NULL,
    price DOUBLE NULL,
    quantity     INT NULL,
    cart_user_id BINARY(16)            NULL,
    CONSTRAINT pk_cart_items PRIMARY KEY (id)
);

CREATE TABLE carts
(
    user_id    BINARY(16) NOT NULL,
    created_at datetime NOT NULL,
    updated_at datetime NULL,
    is_deleted BIT(1)   NOT NULL,
    total_amount DOUBLE NULL,
    CONSTRAINT pk_carts PRIMARY KEY (user_id)
);

ALTER TABLE cart_items
    ADD CONSTRAINT FK_CART_ITEMS_ON_CART_USER FOREIGN KEY (cart_user_id) REFERENCES carts (user_id);