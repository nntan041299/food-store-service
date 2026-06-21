ALTER TABLE inventory_items
    DROP COLUMN IF EXISTS category;

CREATE TABLE categories
(
    id         BIGSERIAL    PRIMARY KEY,
    shop_id    BIGINT       NOT NULL REFERENCES shops (id),
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_categories_shop_id_name UNIQUE (shop_id, name)
);

CREATE INDEX idx_categories_shop_id ON categories (shop_id);

CREATE TABLE inventory_item_categories
(
    inventory_item_id BIGINT NOT NULL REFERENCES inventory_items (id) ON DELETE CASCADE,
    category_id       BIGINT NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    PRIMARY KEY (inventory_item_id, category_id)
);

CREATE INDEX idx_inv_item_categories_category_id ON inventory_item_categories (category_id);
