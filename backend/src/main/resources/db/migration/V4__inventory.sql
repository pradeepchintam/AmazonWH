CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    sku_id BIGINT NOT NULL REFERENCES sku(id),
    location_id BIGINT NOT NULL REFERENCES location(id),
    dc_id BIGINT NOT NULL REFERENCES dc(id),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    lot_number VARCHAR(50),
    qty_on_hand INTEGER NOT NULL DEFAULT 0,
    qty_allocated INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(sku_id, location_id, status, lot_number)
);

CREATE TABLE cycle_count (
    id BIGSERIAL PRIMARY KEY,
    dc_id BIGINT NOT NULL REFERENCES dc(id),
    location_id BIGINT NOT NULL REFERENCES location(id),
    sku_id BIGINT REFERENCES sku(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    system_qty INTEGER,
    counted_qty INTEGER,
    variance INTEGER,
    counted_by BIGINT REFERENCES app_user(id),
    counted_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_sku ON inventory(sku_id);
CREATE INDEX idx_inventory_location ON inventory(location_id);
CREATE INDEX idx_inventory_dc_status ON inventory(dc_id, status);
