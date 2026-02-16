CREATE TABLE pick_task (
    id BIGSERIAL PRIMARY KEY,
    wave_id BIGINT NOT NULL REFERENCES wave(id),
    order_id BIGINT NOT NULL REFERENCES wms_order(id),
    order_line_id BIGINT NOT NULL REFERENCES order_line(id),
    sku_id BIGINT NOT NULL REFERENCES sku(id),
    from_location_id BIGINT NOT NULL REFERENCES location(id),
    dc_id BIGINT NOT NULL REFERENCES dc(id),
    qty_to_pick INTEGER NOT NULL,
    qty_picked INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assigned_to BIGINT REFERENCES app_user(id),
    pick_sequence INTEGER DEFAULT 0,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pick_task_wave ON pick_task(wave_id);
CREATE INDEX idx_pick_task_assigned ON pick_task(assigned_to, status);
