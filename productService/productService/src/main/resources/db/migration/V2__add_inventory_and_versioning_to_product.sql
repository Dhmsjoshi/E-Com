-- 1. Naye Columns add karo
ALTER TABLE product
    ADD COLUMN quantity INT NOT NULL DEFAULT 0,
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- 2. Category table columns modify karo
ALTER TABLE category
    MODIFY name VARCHAR(50) NOT NULL,
    MODIFY description VARCHAR(500);

-- 3. Product table columns modify karo
ALTER TABLE product
    MODIFY title VARCHAR(100) NOT NULL,
    MODIFY description VARCHAR(500),
    MODIFY image_url VARCHAR(1024);