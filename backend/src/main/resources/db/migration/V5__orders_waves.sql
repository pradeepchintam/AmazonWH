CREATE TABLE wms_order (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(30) NOT NULL UNIQUE,
    dc_id BIGINT NOT NULL REFERENCES dc(id),
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    priority INTEGER DEFAULT 5,
    customer_name VARCHAR(100),
    ship_to_address VARCHAR(255),
    ship_to_city VARCHAR(100),
    ship_to_state VARCHAR(50),
    ship_to_zip VARCHAR(20),
    carrier_id BIGINT REFERENCES carrier(id),
    wave_id BIGINT,
    required_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_line (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES wms_order(id),
    sku_id BIGINT NOT NULL REFERENCES sku(id),
    line_number INTEGER NOT NULL,
    ordered_qty INTEGER NOT NULL,
    allocated_qty INTEGER NOT NULL DEFAULT 0,
    picked_qty INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wave (
    id BIGSERIAL PRIMARY KEY,
    wave_number VARCHAR(30) NOT NULL UNIQUE,
    dc_id BIGINT NOT NULL REFERENCES dc(id),
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    released_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE wms_order ADD CONSTRAINT fk_order_wave FOREIGN KEY (wave_id) REFERENCES wave(id);
