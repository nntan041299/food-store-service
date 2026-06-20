CREATE TABLE shops
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    description  TEXT,
    address      VARCHAR(255),
    phone_number VARCHAR(20),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    owner_id     BIGINT       NOT NULL REFERENCES users (id),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100)
);

CREATE INDEX idx_shops_owner_id ON shops (owner_id);
