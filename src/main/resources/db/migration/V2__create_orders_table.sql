-- Migration: Create orders table for production order management
-- Author: System
-- Date: 2026-04-27

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) UNIQUE NOT NULL COMMENT 'Business key: ORD-2026-045',
    product_id BIGINT NOT NULL,
    routing_id BIGINT NOT NULL COMMENT 'FK to APPROVED routing only',
    order_qty INT NOT NULL CHECK (order_qty > 0),
    production_start_date DATE NULL COMMENT 'When production actually started',
    expected_completion_date DATE NULL,
    customer_name VARCHAR(255) NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT, ACTIVE, COMPLETED, ON_HOLD, CANCELLED',
    created_by BIGINT NOT NULL COMMENT 'GM employee_id',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES product(product_id),
    CONSTRAINT fk_orders_routing FOREIGN KEY (routing_id) REFERENCES routing(routing_id),
    CONSTRAINT fk_orders_created_by FOREIGN KEY (created_by) REFERENCES employee(emp_id),
    
    INDEX idx_order_number (order_number),
    INDEX idx_product_id (product_id),
    INDEX idx_routing_id (routing_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_production_start_date (production_start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Production orders linking products to approved process plans';

-- Add order_id foreign key to bin table
-- This links bins to orders for order-level tracking
ALTER TABLE bin 
ADD COLUMN order_id BIGINT NULL COMMENT 'FK to orders table - links bin to production order',
ADD CONSTRAINT fk_bin_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE SET NULL;

CREATE INDEX idx_bin_order_id ON bin(order_id);

-- Add trigger to validate only APPROVED routings can be linked to orders
DELIMITER $$

CREATE TRIGGER trg_orders_validate_routing_approved
BEFORE INSERT ON orders
FOR EACH ROW
BEGIN
    DECLARE routing_approval_status VARCHAR(255);
    
    SELECT approval_status INTO routing_approval_status
    FROM routing
    WHERE routing_id = NEW.routing_id;
    
    IF routing_approval_status != 'APPROVED' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Only APPROVED routings can be linked to orders';
    END IF;
END$$

CREATE TRIGGER trg_orders_validate_routing_approved_update
BEFORE UPDATE ON orders
FOR EACH ROW
BEGIN
    DECLARE routing_approval_status VARCHAR(255);
    
    IF NEW.routing_id != OLD.routing_id THEN
        SELECT approval_status INTO routing_approval_status
        FROM routing
        WHERE routing_id = NEW.routing_id;
        
        IF routing_approval_status != 'APPROVED' THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Only APPROVED routings can be linked to orders';
        END IF;
    END IF;
END$$

DELIMITER ;
