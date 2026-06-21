-- Migrate existing default 'pcs' value to enum name 'PIECE'
UPDATE inventory_items SET unit = 'PIECE' WHERE unit = 'pcs';

-- Change column type to VARCHAR(20) storing enum string values and update default
ALTER TABLE inventory_items
    ALTER COLUMN unit TYPE VARCHAR(20),
    ALTER COLUMN unit SET DEFAULT 'PIECE';
