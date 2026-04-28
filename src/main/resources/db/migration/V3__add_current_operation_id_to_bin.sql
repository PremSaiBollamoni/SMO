-- Add current_operation_id to bin table for workflow progression tracking
ALTER TABLE bin ADD COLUMN current_operation_id BIGINT NULL;

-- Add comment for clarity
ALTER TABLE bin MODIFY COLUMN current_operation_id BIGINT NULL COMMENT 'Current operation in routing sequence (NULL = not started or completed)';

-- Add comment to last_operation_id for distinction
ALTER TABLE bin MODIFY COLUMN last_operation_id BIGINT NULL COMMENT 'Last completed operation (for metrics/history)';
