CREATE TABLE shipment (
    id BIGSERIAL PRIMARY KEY,
    shipment_number VARCHAR(30) NOT NULL UNIQUE,
    dc_id BIGINT NOT NULL REFERENCES dc(id),
    carrier_id BIGINT REFERENCES carrier(id),
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    bol_number VARCHAR(50),
    trailer_number VARCHAR(50),
    door_number VARCHAR(20),
    shipped_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shipment_line (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipment(id),
    order_id BIGINT NOT NULL REFERENCES wms_order(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
