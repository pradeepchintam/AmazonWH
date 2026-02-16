-- Roles
INSERT INTO role (name) VALUES ('ADMIN'), ('MANAGER'), ('RECEIVER'), ('PICKER'), ('SHIPPER');

-- Sample DC
INSERT INTO dc (code, name, address, city, state, zip) VALUES ('DC001', 'Main Distribution Center', '123 Warehouse Blvd', 'Seattle', 'WA', '98101');

-- Sample locations for DC001 (id=1)
INSERT INTO location (dc_id, code, zone, aisle, bay, level, position, loc_type, pick_sequence) VALUES
(1, 'RCV-DOCK-01', 'RECEIVING', NULL, NULL, NULL, NULL, 'RECEIVING', 0),
(1, 'RCV-DOCK-02', 'RECEIVING', NULL, NULL, NULL, NULL, 'RECEIVING', 0),
(1, 'A-01-01-A', 'ZONE-A', 'A', '01', '01', 'A', 'STORAGE', 100),
(1, 'A-01-01-B', 'ZONE-A', 'A', '01', '01', 'B', 'STORAGE', 101),
(1, 'A-01-02-A', 'ZONE-A', 'A', '01', '02', 'A', 'STORAGE', 102),
(1, 'A-02-01-A', 'ZONE-A', 'A', '02', '01', 'A', 'STORAGE', 200),
(1, 'B-01-01-A', 'ZONE-B', 'B', '01', '01', 'A', 'STORAGE', 300),
(1, 'B-01-01-B', 'ZONE-B', 'B', '01', '01', 'B', 'STORAGE', 301),
(1, 'B-01-02-A', 'ZONE-B', 'B', '01', '02', 'A', 'STORAGE', 302),
(1, 'SHP-DOCK-01', 'SHIPPING', NULL, NULL, NULL, NULL, 'SHIPPING', 900),
(1, 'SHP-DOCK-02', 'SHIPPING', NULL, NULL, NULL, NULL, 'SHIPPING', 901);

-- Admin user (password: admin123)
INSERT INTO app_user (username, password, full_name, email, dc_id)
VALUES ('admin', '$2b$10$e3O7bmJEde.lDuMhHwrsFez9Ryjljzw06WdJsIwjtGuIBPkHqhw2i', 'System Admin', 'admin@amazonwh.com', 1);
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
