CREATE TABLE inventory_items
(
    id                  BIGSERIAL PRIMARY KEY,
    shop_id             BIGINT         NOT NULL REFERENCES shops (id),
    name                VARCHAR(150)   NOT NULL,
    description         TEXT,
    category            VARCHAR(100),
    price               NUMERIC(10, 2) NOT NULL,
    unit                VARCHAR(20)    NOT NULL DEFAULT 'pcs',
    quantity            INTEGER        NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER,
    image_url           VARCHAR(255),
    is_available        BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP      NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);

CREATE INDEX idx_inventory_items_shop_id ON inventory_items (shop_id);
CREATE UNIQUE INDEX uq_inventory_items_shop_id_name ON inventory_items (shop_id, name);
