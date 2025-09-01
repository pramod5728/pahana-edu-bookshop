-- Pahana Edu Bookshop Database Schema
-- Create database
CREATE DATABASE IF NOT EXISTS pahana_bookshop
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE pahana_bookshop;

-- Drop tables if they exist (for clean installation)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS bill_items;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'MANAGER', 'EMPLOYEE', 'VIEWER') NOT NULL DEFAULT 'EMPLOYEE',
    phone VARCHAR(15),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login DATETIME,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    account_locked_until DATETIME,
    password_changed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_username (username),
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
);

-- Create customers table
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_customer_account_number (account_number),
    INDEX idx_customer_name (name),
    INDEX idx_customer_phone (phone),
    INDEX idx_customer_email (email)
);

-- Create items table
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL,
    category VARCHAR(50),
    minimum_stock_level INT NOT NULL DEFAULT 10,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_item_code (item_code),
    INDEX idx_item_name (name),
    INDEX idx_item_category (category),
    INDEX idx_item_active (active)
);

-- Create bills table
CREATE TABLE bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    bill_date DATETIME NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    tax_rate DECIMAL(5, 4) NOT NULL DEFAULT 0.1500,
    tax_amount DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12, 2) NOT NULL,
    status ENUM('DRAFT', 'PENDING', 'PAID', 'CANCELLED', 'PARTIAL_PAID', 'OVERDUE') NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_bill_number (bill_number),
    INDEX idx_bill_customer (customer_id),
    INDEX idx_bill_date (bill_date),
    INDEX idx_bill_status (status)
);

-- Create bill_items table
CREATE TABLE bill_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(12, 2) NOT NULL,
    
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id),
    INDEX idx_bill_item_bill (bill_id),
    INDEX idx_bill_item_item (item_id)
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password_hash, full_name, email, role, phone, password_changed_at) VALUES
('admin', '$2a$10$9B5pZgzJfS5qB8ZMiOjNNufO8fX8WV/hGqnC5zZ8qBo.F8XVnBzRC', 'System Administrator', 'admin@pahanaedu.com', 'ADMIN', '+94771234567', CURRENT_TIMESTAMP);

-- Insert sample managers
INSERT INTO users (username, password_hash, full_name, email, role, phone, password_changed_at) VALUES
('manager', '$2a$10$9B5pZgzJfS5qB8ZMiOjNNufO8fX8WV/hGqnC5zZ8qBo.F8XVnBzRC', 'Store Manager', 'manager@pahanaedu.com', 'MANAGER', '+94771234568', CURRENT_TIMESTAMP),
('employee', '$2a$10$9B5pZgzJfS5qB8ZMiOjNNufO8fX8WV/hGqnC5zZ8qBo.F8XVnBzRC', 'Sales Employee', 'employee@pahanaedu.com', 'EMPLOYEE', '+94771234569', CURRENT_TIMESTAMP);

-- Insert sample customers
INSERT INTO customers (account_number, name, address, phone, email) VALUES
('CUST-20241201-A001', 'John Doe', '123 Main Street, Colombo 01, Sri Lanka', '+94771111111', 'john.doe@email.com'),
('CUST-20241201-A002', 'Jane Smith', '456 Galle Road, Colombo 03, Sri Lanka', '+94772222222', 'jane.smith@email.com'),
('CUST-20241201-A003', 'David Wilson', '789 Kandy Road, Colombo 07, Sri Lanka', '+94773333333', 'david.wilson@email.com'),
('CUST-20241201-A004', 'Sarah Johnson', '321 Hospital Road, Kalubowila, Sri Lanka', '+94774444444', 'sarah.johnson@email.com'),
('CUST-20241201-A005', 'Michael Brown', '654 Baseline Road, Colombo 09, Sri Lanka', '+94775555555', 'michael.brown@email.com'),
('CUST-20241201-A006', 'Emily Davis', '987 Union Place, Colombo 02, Sri Lanka', '+94776666666', 'emily.davis@email.com'),
('CUST-20241201-A007', 'Robert Miller', '147 Reid Avenue, Colombo 04, Sri Lanka', '+94777777777', 'robert.miller@email.com'),
('CUST-20241201-A008', 'Lisa Anderson', '258 High Level Road, Maharagama, Sri Lanka', '+94778888888', 'lisa.anderson@email.com'),
('CUST-20241201-A009', 'William Garcia', '369 Negombo Road, Wattala, Sri Lanka', '+94779999999', 'william.garcia@email.com'),
('CUST-20241201-A010', 'Jennifer Martinez', '741 Gampaha Road, Ragama, Sri Lanka', '+94771010101', 'jennifer.martinez@email.com');

-- Insert sample items (educational books and supplies)
INSERT INTO items (item_code, name, description, price, stock_quantity, category, minimum_stock_level) VALUES
-- Textbooks
('BOOK-MATH-001', 'Advanced Mathematics Grade 12', 'Comprehensive mathematics textbook for Grade 12 students', 1250.00, 50, 'Textbooks', 10),
('BOOK-PHYS-001', 'Physics Fundamentals Grade 11', 'Physics textbook covering fundamental concepts', 1100.00, 45, 'Textbooks', 10),
('BOOK-CHEM-001', 'Chemistry Grade 10', 'Introduction to chemistry for Grade 10 students', 950.00, 40, 'Textbooks', 8),
('BOOK-BIO-001', 'Biology Essentials Grade 9', 'Biology textbook for Grade 9 curriculum', 900.00, 35, 'Textbooks', 8),
('BOOK-ENG-001', 'English Literature Collection', 'Selected English literature for advanced students', 800.00, 30, 'Textbooks', 5),

-- Reference Books
('REF-DICT-001', 'Oxford English Dictionary', 'Comprehensive English dictionary', 2500.00, 25, 'Reference', 5),
('REF-ENCY-001', 'Encyclopedia Britannica Set', 'Complete encyclopedia set (10 volumes)', 15000.00, 5, 'Reference', 2),
('REF-ATLAS-001', 'World Atlas 2024', 'Latest world atlas with updated maps', 1800.00, 20, 'Reference', 5),

-- Stationery
('STAT-PEN-001', 'Blue Ink Pens (Pack of 10)', 'High-quality blue ink pens', 250.00, 100, 'Stationery', 20),
('STAT-PENCIL-001', 'HB Pencils (Pack of 12)', 'Standard HB pencils for writing', 180.00, 120, 'Stationery', 25),
('STAT-RULER-001', '30cm Plastic Ruler', 'Transparent plastic ruler', 75.00, 80, 'Stationery', 15),
('STAT-ERASER-001', 'Erasers (Pack of 5)', 'White erasers for pencil marks', 100.00, 90, 'Stationery', 20),
('STAT-NOTEBOOK-001', 'Exercise Books (Pack of 10)', 'Ruled exercise books', 350.00, 150, 'Stationery', 30),

-- Digital Learning
('DIGI-CALC-001', 'Scientific Calculator', 'Advanced scientific calculator', 2200.00, 30, 'Electronics', 8),
('DIGI-TABLET-001', 'Educational Tablet', 'Tablet preloaded with educational content', 25000.00, 15, 'Electronics', 3),

-- Art Supplies
('ART-COLOR-001', 'Color Pencil Set (24 colors)', 'Professional color pencil set', 1200.00, 40, 'Art Supplies', 10),
('ART-PAINT-001', 'Watercolor Paint Set', 'Complete watercolor painting set', 1500.00, 25, 'Art Supplies', 5),
('ART-BRUSH-001', 'Paint Brush Set', 'Various sized paint brushes', 800.00, 35, 'Art Supplies', 8);

-- Insert sample bills
INSERT INTO bills (bill_number, customer_id, bill_date, subtotal, tax_amount, total_amount, status) VALUES
('BILL000001', 1, '2024-11-15 10:30:00', 2500.00, 375.00, 2875.00, 'PAID'),
('BILL000002', 2, '2024-11-20 14:15:00', 1800.00, 270.00, 2070.00, 'PAID'),
('BILL000003', 3, '2024-11-25 09:45:00', 3200.00, 480.00, 3680.00, 'PENDING'),
('BILL000004', 4, '2024-11-28 16:20:00', 950.00, 142.50, 1092.50, 'PAID'),
('BILL000005', 5, '2024-12-01 11:10:00', 1750.00, 262.50, 2012.50, 'PENDING');

-- Insert sample bill items
INSERT INTO bill_items (bill_id, item_id, quantity, unit_price, total_price) VALUES
-- Bill 1 items
(1, 1, 2, 1250.00, 2500.00),

-- Bill 2 items  
(2, 3, 1, 950.00, 950.00),
(2, 11, 1, 75.00, 75.00),
(2, 9, 3, 250.00, 750.00),

-- Bill 3 items
(3, 2, 1, 1100.00, 1100.00),
(3, 4, 1, 900.00, 900.00),
(3, 15, 1, 2200.00, 2200.00),

-- Bill 4 items
(4, 5, 1, 800.00, 800.00),
(4, 12, 1, 100.00, 100.00),

-- Bill 5 items
(5, 6, 1, 2500.00, 2500.00),
(5, 10, 5, 180.00, 900.00),
(5, 13, 1, 350.00, 350.00);

-- Create indexes for better performance
CREATE INDEX idx_bill_items_composite ON bill_items(bill_id, item_id);
CREATE INDEX idx_items_stock_active ON items(stock_quantity, active);
CREATE INDEX idx_bills_date_status ON bills(bill_date, status);
CREATE INDEX idx_customers_name_active ON customers(name);

-- Show table creation summary
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'pahana_bookshop'
ORDER BY TABLE_NAME;