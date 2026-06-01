CREATE TABLE IF NOT EXISTS accident_report (
    report_no VARCHAR(50) PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL,
    accident_at TIMESTAMP NOT NULL,
    accident_description TEXT NOT NULL,
    damage_details TEXT NOT NULL,
    accident_type VARCHAR(50) NOT NULL,
    accident_status VARCHAR(50) NOT NULL,
    accident_report_document_name VARCHAR(255),
    medical_certificate_file_name VARCHAR(255),
    claim_document_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS accident_at TIMESTAMP;
ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS accident_type VARCHAR(50);
ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS accident_report_document_name VARCHAR(255);
ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS medical_certificate_file_name VARCHAR(255);
ALTER TABLE accident_report ADD COLUMN IF NOT EXISTS claim_document_name VARCHAR(255);
ALTER TABLE accident_report ALTER COLUMN accident_status SET DEFAULT 'RECEIVED';

UPDATE accident_report
SET accident_type = accident_status
WHERE accident_type IS NULL
  AND accident_status IN ('VEHICLE', 'PROPERTY', 'PERSONAL_INJURY', 'NATURAL_DISASTER');

UPDATE accident_report
SET accident_status = 'RECEIVED'
WHERE accident_status IS NULL
   OR accident_status IN ('VEHICLE', 'PROPERTY', 'PERSONAL_INJURY', 'NATURAL_DISASTER');

CREATE TABLE IF NOT EXISTS damage_investigation (
    investigation_id VARCHAR(50) PRIMARY KEY,
    report_no VARCHAR(50) NOT NULL,
    adjuster_id VARCHAR(50) NOT NULL,
    investigation_at TIMESTAMP NOT NULL,
    medical_expense NUMERIC(15,2) NOT NULL,
    lost_income NUMERIC(15,2) NOT NULL,
    repair_cost NUMERIC(15,2) NOT NULL,
    settlement_amount NUMERIC(15,2) NOT NULL,
    fault_ratio REAL NOT NULL,
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS investigation_id VARCHAR(50);
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS report_no VARCHAR(50);
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS adjuster_id VARCHAR(50);
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS investigation_at TIMESTAMP;
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS medical_expense NUMERIC(15,2);
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS lost_income NUMERIC(15,2);
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS repair_cost NUMERIC(15,2);
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS settlement_amount NUMERIC(15,2);
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS fault_ratio REAL;
ALTER TABLE damage_investigation ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS payment_approval_document (
    document_id VARCHAR(50) PRIMARY KEY,
    report_no VARCHAR(50) NOT NULL,
    investigation_id VARCHAR(50) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    submission_status VARCHAR(50) NOT NULL,
    damage_amount NUMERIC(15,2),
    fault_ratio REAL,
    fault_ratio_opinion TEXT,
    adjuster_opinion TEXT,
    employee_no VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    submitted_at TIMESTAMP
);

ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS document_id VARCHAR(50);
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS report_no VARCHAR(50);
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS investigation_id VARCHAR(50);
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS document_type VARCHAR(50);
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS submission_status VARCHAR(50);
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS damage_amount NUMERIC(15,2);
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS fault_ratio REAL;
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS fault_ratio_opinion TEXT;
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS adjuster_opinion TEXT;
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS employee_no VARCHAR(50);
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE payment_approval_document ADD COLUMN IF NOT EXISTS submitted_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_damage_investigation_report_no
    ON damage_investigation (report_no);

CREATE INDEX IF NOT EXISTS idx_payment_approval_document_report_no
    ON payment_approval_document (report_no);

CREATE TABLE IF NOT EXISTS subrogation (
    subrogation_id VARCHAR(50) PRIMARY KEY,
    report_no VARCHAR(50) NOT NULL,
    document_id VARCHAR(50) NOT NULL,
    investigation_id VARCHAR(50) NOT NULL,
    offender_name VARCHAR(255) NOT NULL,
    subrogation_reason TEXT NOT NULL,
    subrogation_amount NUMERIC(15,2) NOT NULL,
    employee_no VARCHAR(50) NOT NULL,
    subrogation_status VARCHAR(50) NOT NULL,
    recovered_amount NUMERIC(15,2),
    recovered_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS subrogation_id VARCHAR(50);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS report_no VARCHAR(50);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS document_id VARCHAR(50);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS investigation_id VARCHAR(50);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS offender_name VARCHAR(255);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS subrogation_reason TEXT;
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS subrogation_amount NUMERIC(15,2);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS employee_no VARCHAR(50);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS subrogation_status VARCHAR(50);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS recovered_amount NUMERIC(15,2);
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS recovered_at TIMESTAMP;
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE subrogation ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_subrogation_report_no
    ON subrogation (report_no);

CREATE TABLE IF NOT EXISTS objection (
    objection_id VARCHAR(50) PRIMARY KEY,
    accident_number VARCHAR(50) NOT NULL,
    claimant_name VARCHAR(100) NOT NULL,
    claimant_phone VARCHAR(50) NOT NULL,
    objection_reason TEXT NOT NULL,
    requested_action TEXT NOT NULL,
    employee_no VARCHAR(50) NOT NULL,
    objection_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP,
    completed_at TIMESTAMP
);

ALTER TABLE objection ADD COLUMN IF NOT EXISTS objection_id VARCHAR(50);
ALTER TABLE objection ADD COLUMN IF NOT EXISTS accident_number VARCHAR(50);
ALTER TABLE objection ADD COLUMN IF NOT EXISTS claimant_name VARCHAR(100);
ALTER TABLE objection ADD COLUMN IF NOT EXISTS claimant_phone VARCHAR(50);
ALTER TABLE objection ADD COLUMN IF NOT EXISTS objection_reason TEXT;
ALTER TABLE objection ADD COLUMN IF NOT EXISTS requested_action TEXT;
ALTER TABLE objection ADD COLUMN IF NOT EXISTS employee_no VARCHAR(50);
ALTER TABLE objection ADD COLUMN IF NOT EXISTS objection_status VARCHAR(50);
ALTER TABLE objection ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE objection ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
ALTER TABLE objection ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMP;
ALTER TABLE objection ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_objection_accident_number
    ON objection (accident_number);

CREATE TABLE IF NOT EXISTS product (
    product_code VARCHAR(50) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    insurance_type VARCHAR(50) NOT NULL,
    target_customer VARCHAR(255),
    sales_channel VARCHAR(255),
    insurance_period VARCHAR(100),
    payment_period VARCHAR(100),
    insured_amount NUMERIC(15,2),
    premium NUMERIC(15,2),
    maturity_refund NUMERIC(15,2),
    main_coverage TEXT,
    subscription_conditions TEXT,
    rate_information TEXT,
    special_contract_info TEXT,
    product_status VARCHAR(50) NOT NULL,
    driver_age INTEGER,
    vehicle_type VARCHAR(100),
    building_type VARCHAR(100),
    location VARCHAR(255),
    shipping_route VARCHAR(255),
    vessel_type VARCHAR(100),
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE product ADD COLUMN IF NOT EXISTS product_code VARCHAR(50);
ALTER TABLE product ADD COLUMN IF NOT EXISTS product_name VARCHAR(255);
ALTER TABLE product ADD COLUMN IF NOT EXISTS insurance_type VARCHAR(50);
ALTER TABLE product ADD COLUMN IF NOT EXISTS target_customer VARCHAR(255);
ALTER TABLE product ADD COLUMN IF NOT EXISTS sales_channel VARCHAR(255);
ALTER TABLE product ADD COLUMN IF NOT EXISTS insurance_period VARCHAR(100);
ALTER TABLE product ADD COLUMN IF NOT EXISTS payment_period VARCHAR(100);
ALTER TABLE product ADD COLUMN IF NOT EXISTS insured_amount NUMERIC(15,2);
ALTER TABLE product ADD COLUMN IF NOT EXISTS premium NUMERIC(15,2);
ALTER TABLE product ADD COLUMN IF NOT EXISTS maturity_refund NUMERIC(15,2);
ALTER TABLE product ADD COLUMN IF NOT EXISTS main_coverage TEXT;
ALTER TABLE product ADD COLUMN IF NOT EXISTS subscription_conditions TEXT;
ALTER TABLE product ADD COLUMN IF NOT EXISTS rate_information TEXT;
ALTER TABLE product ADD COLUMN IF NOT EXISTS special_contract_info TEXT;
ALTER TABLE product ADD COLUMN IF NOT EXISTS product_status VARCHAR(50);
ALTER TABLE product ADD COLUMN IF NOT EXISTS driver_age INTEGER;
ALTER TABLE product ADD COLUMN IF NOT EXISTS vehicle_type VARCHAR(100);
ALTER TABLE product ADD COLUMN IF NOT EXISTS building_type VARCHAR(100);
ALTER TABLE product ADD COLUMN IF NOT EXISTS location VARCHAR(255);
ALTER TABLE product ADD COLUMN IF NOT EXISTS shipping_route VARCHAR(255);
ALTER TABLE product ADD COLUMN IF NOT EXISTS vessel_type VARCHAR(100);
ALTER TABLE product ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_product_insurance_type
    ON product (insurance_type);

CREATE TABLE IF NOT EXISTS product_authorization (
    request_id VARCHAR(50) PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    request_reason TEXT,
    submission_agency_name VARCHAR(255),
    authorization_status VARCHAR(50) NOT NULL,
    product_description_file_name VARCHAR(255),
    terms_and_conditions_file_name VARCHAR(255),
    rate_schedule_file_name VARCHAR(255),
    product_evidence_file_name VARCHAR(255),
    revision_request TEXT,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS request_id VARCHAR(50);
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS product_code VARCHAR(50);
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS requested_at TIMESTAMP;
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP;
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS is_approved BOOLEAN;
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS request_reason TEXT;
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS submission_agency_name VARCHAR(255);
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS authorization_status VARCHAR(50);
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS product_description_file_name VARCHAR(255);
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS terms_and_conditions_file_name VARCHAR(255);
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS rate_schedule_file_name VARCHAR(255);
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS product_evidence_file_name VARCHAR(255);
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS revision_request TEXT;
ALTER TABLE product_authorization ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_product_authorization_product_code
    ON product_authorization (product_code);
