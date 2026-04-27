-- Test Orders Seed Data
-- Purpose: Create sample orders for testing Strategic Monitor

-- Insert test orders (using existing product_id=501, routing_id=3 which is APPROVED)
INSERT INTO orders (order_number, product_id, routing_id, order_qty, expected_completion_date, customer_name, status, created_by, created_at, updated_at)
VALUES 
('ORD-2026-001', 501, 3, 500, '2026-05-15', 'ABC Garments Ltd', 'ACTIVE', 1003, NOW(), NOW()),
('ORD-2026-002', 501, 3, 300, '2026-05-20', 'XYZ Fashion House', 'ACTIVE', 1003, NOW(), NOW()),
('ORD-2026-003', 501, 3, 750, '2026-06-01', 'Global Retail Co', 'DRAFT', 1003, NOW(), NOW());

-- Update production_start_date for active orders
UPDATE orders SET production_start_date = '2026-04-20' WHERE order_number = 'ORD-2026-001';
UPDATE orders SET production_start_date = '2026-04-22' WHERE order_number = 'ORD-2026-002';

SELECT 'Test orders created successfully!' AS status;
SELECT * FROM orders;
