CREATE TABLE purchase_order (
    id BIGSERIAL PRIMARY KEY,
    po_number VARCHAR(30) NOT NULL UNIQUE,
    vendor_id BIGINT NOT NULL REFERENCES vendor(id),
    dc_id BIGINT NOT NULL REFERENCES dc(id),
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    expected_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE purchase_order_line (
    id BIGSERIAL PRIMARY KEY,
    po_id BIGINT NOT NULL REFERENCES purchase_order(id),
    sku_id BIGINT NOT NULL REFERENCES sku(id),
    expected_qty INTEGER NOT NULL,
    received_qty INTEGER NOT NULL DEFAULT 0,
    line_number INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE asn (
    id BIGSERIAL PRIMARY KEY,
    asn_number VARCHAR(30) NOT NULL UNIQUE,
    po_id BIGINT NOT NULL REFERENCES purchase_order(id),
    carrier_id BIGINT REFERENCES carrier(id),
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    expected_arrival DATE,
    arrived_at TIMESTAMP,
    trailer_number VARCHAR(50),
    door_number VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE asn_line (
    id BIGSERIAL PRIMARY KEY,
    asn_id BIGINT NOT NULL REFERENCES asn(id),
    po_line_id BIGINT NOT NULL REFERENCES purchase_order_line(id),
    sku_id BIGINT NOT NULL REFERENCES sku(id),
    expected_qty INTEGER NOT NULL,
    received_qty INTEGER NOT NULL DEFAULT 0,
    lot_number VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
