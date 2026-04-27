-- End-to-End Order Management Test
-- Tests complete flow: Order → Bin Assignment → WIP Tracking → Progress Calculation

-- Step 1: Verify order exists
SELECT 'Step 1: Verify Order ORD-2026-001 exists' AS test_step;
SELECT order_id, order_number, order_qty, status FROM orders WHERE order_number = 'ORD-2026-001';

-- Step 2: Create test bins linked to order
SELECT 'Step 2: Creating test bins linked to order_id=4' AS test_step;

-- Get next bin IDs
SET @next_bin_id = (SELECT COALESCE(MAX(bin_id), 0) + 1 FROM bin);

-- Create 3 test bins for order ORD-2026-001 (order_id=4)
INSERT INTO bin (bin_id, qr_code, qty, status, current_status, current_routing_id, order_id, created_at)
VALUES 
(@next_bin_id, 'QR-TEST-001', 100, 'assigned', 'assigned', 3, 4, NOW()),
(@next_bin_id + 1, 'QR-TEST-002', 150, 'assigned', 'assigned', 3, 4, NOW()),
(@next_bin_id + 2, 'QR-TEST-003', 120, 'assigned', 'assigned', 3, 4, NOW());

SELECT 'Bins created:' AS status;
SELECT bin_id, qr_code, qty, order_id FROM bin WHERE order_id = 4;

-- Step 3: Simulate WIP tracking - bins completing terminal operation (operation_id=51 is last step)
SELECT 'Step 3: Creating WIP tracking records for terminal operation' AS test_step;

-- Get next wip IDs
SET @next_wip_id = (SELECT COALESCE(MAX(wip_id), 0) + 1 FROM wiptracking);

-- Simulate 2 bins completed at terminal operation (operation_id=51)
INSERT INTO wiptracking (wip_id, bin_id, operation_id, operator_id, start_time, end_time, qty, status)
VALUES 
(@next_wip_id, @next_bin_id, 51, 1002, '2026-04-27 10:00:00', '2026-04-27 11:30:00', 100, 'completed'),
(@next_wip_id + 1, @next_bin_id + 1, 51, 1002, '2026-04-27 10:00:00', '2026-04-27 12:00:00', 150, 'completed');

-- Bin 3 still in progress (not at terminal operation yet)
INSERT INTO wiptracking (wip_id, bin_id, operation_id, operator_id, start_time, end_time, qty, status)
VALUES 
(@next_wip_id + 2, @next_bin_id + 2, 45, 1002, '2026-04-27 10:00:00', NULL, 120, 'in_progress');

SELECT 'WIP tracking records created:' AS status;
SELECT wip_id, bin_id, operation_id, qty, status FROM wiptracking WHERE bin_id IN (@next_bin_id, @next_bin_id + 1, @next_bin_id + 2);

-- Step 4: Verify progress calculation
SELECT 'Step 4: Progress Calculation' AS test_step;
SELECT 
    'Order ORD-2026-001' AS order_number,
    500 AS order_qty,
    SUM(CASE WHEN w.operation_id = 51 AND w.status = 'completed' THEN w.qty ELSE 0 END) AS completed_qty,
    500 - SUM(CASE WHEN w.operation_id = 51 AND w.status = 'completed' THEN w.qty ELSE 0 END) AS pending_qty,
    ROUND((SUM(CASE WHEN w.operation_id = 51 AND w.status = 'completed' THEN w.qty ELSE 0 END) * 100.0 / 500), 1) AS progress_percent
FROM bin b
LEFT JOIN wiptracking w ON b.bin_id = w.bin_id
WHERE b.order_id = 4;

-- Step 5: Show bin-order linkage
SELECT 'Step 5: Bin-Order Linkage Verification' AS test_step;
SELECT 
    b.bin_id,
    b.qr_code,
    b.qty AS bin_qty,
    b.order_id,
    o.order_number,
    o.order_qty,
    w.operation_id,
    w.status AS wip_status,
    w.qty AS completed_qty
FROM bin b
INNER JOIN orders o ON b.order_id = o.order_id
LEFT JOIN wiptracking w ON b.bin_id = w.bin_id
WHERE o.order_number = 'ORD-2026-001'
ORDER BY b.bin_id;

SELECT 'End-to-End Test Complete!' AS status;
SELECT 'Expected: 250 completed (100+150), 250 pending (120 still in progress), 50% progress' AS expected_result;
