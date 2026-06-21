CREATE TABLE orders
(
    id           BIGSERIAL      PRIMARY KEY,
    shop_id      BIGINT         NOT NULL REFERENCES shops (id),
    customer_id  BIGINT         NOT NULL REFERENCES users (id),
    table_number VARCHAR(20)    NOT NULL,
    status       VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    note         TEXT,
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100)
);

CREATE INDEX idx_orders_shop_id        ON orders (shop_id);
CREATE INDEX idx_orders_shop_id_status ON orders (shop_id, status);
CREATE INDEX idx_orders_customer_id    ON orders (customer_id);

CREATE TABLE order_items
(
    id                BIGSERIAL      PRIMARY KEY,
    order_id          BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    inventory_item_id BIGINT         NOT NULL REFERENCES inventory_items (id),
    item_name         VARCHAR(150)   NOT NULL,
    quantity          INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price        NUMERIC(10, 2) NOT NULL,
    subtotal          NUMERIC(12, 2) NOT NULL,
    created_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100)
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
