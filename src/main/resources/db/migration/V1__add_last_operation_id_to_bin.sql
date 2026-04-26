-- Migration: Add last_operation_id to bin table
-- Purpose: Tracks which operation a bin last completed, enabling node metrics queries.
-- Safe: Column is nullable; existing rows default to NULL (no data loss).

ALTER TABLE bin
    ADD COLUMN IF NOT EXISTS last_operation_id BIGINT DEFAULT NULL;
