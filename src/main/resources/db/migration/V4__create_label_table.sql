-- Create label table for master data management
CREATE TABLE IF NOT EXISTS label (
    label_id BIGINT NOT NULL PRIMARY KEY,
    label_code VARCHAR(255),
    label_name VARCHAR(255),
    label_type VARCHAR(255),
    description TEXT,
    status VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6)
);
