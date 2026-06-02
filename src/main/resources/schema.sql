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

CREATE TABLE IF NOT EXISTS insurance_application (
    application_id VARCHAR(50) PRIMARY KEY,
    application_status VARCHAR(50) NOT NULL,
    applied_at TIMESTAMP NOT NULL,
    applied_condition TEXT,
    insured_amount NUMERIC(15,2) NOT NULL,
    insured_person_info TEXT NOT NULL,
    payment_cycle VARCHAR(50) NOT NULL,
    premium NUMERIC(15,2) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    special_contract_list VARCHAR(100),
    terms_version VARCHAR(100) NOT NULL,
    insured_person_name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL,
    gender VARCHAR(20) NOT NULL,
    occupation VARCHAR(100) NOT NULL,
    annual_income NUMERIC(15,2) NOT NULL,
    past_medical_history TEXT,
    is_medicated BOOLEAN NOT NULL,
    surgery_history TEXT,
    family_history TEXT,
    is_smoker BOOLEAN NOT NULL,
    alcohol_consumption VARCHAR(100),
    bmi NUMERIC(5,2) NOT NULL,
    vehicle_model VARCHAR(100),
    vehicle_number VARCHAR(50),
    has_accident_history BOOLEAN NOT NULL,
    has_other_contract BOOLEAN NOT NULL
);

ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS application_id VARCHAR(50);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS application_status VARCHAR(50);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS applied_at TIMESTAMP;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS applied_condition TEXT;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS insured_amount NUMERIC(15,2);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS insured_person_info TEXT;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS payment_cycle VARCHAR(50);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS premium NUMERIC(15,2);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS product_code VARCHAR(50);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS special_contract_list VARCHAR(100);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS terms_version VARCHAR(100);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS insured_person_name VARCHAR(100);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS age INTEGER;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS gender VARCHAR(20);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS occupation VARCHAR(100);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS annual_income NUMERIC(15,2);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS past_medical_history TEXT;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS is_medicated BOOLEAN;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS surgery_history TEXT;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS family_history TEXT;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS is_smoker BOOLEAN;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS alcohol_consumption VARCHAR(100);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS bmi NUMERIC(5,2);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS vehicle_model VARCHAR(100);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS vehicle_number VARCHAR(50);
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS has_accident_history BOOLEAN;
ALTER TABLE insurance_application ADD COLUMN IF NOT EXISTS has_other_contract BOOLEAN;

CREATE TABLE IF NOT EXISTS underwriting_review (
    review_id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    underwriting_status VARCHAR(50) NOT NULL,
    underwriting_type VARCHAR(50) NOT NULL,
    auto_score REAL NOT NULL,
    total_deduction INTEGER NOT NULL,
    recommended_result VARCHAR(50) NOT NULL,
    final_result VARCHAR(50),
    is_auto_review_available BOOLEAN NOT NULL,
    is_coinsurance_recommended BOOLEAN NOT NULL,
    itemized_scores TEXT NOT NULL,
    underwriter_id VARCHAR(50),
    underwriter_name VARCHAR(100),
    department VARCHAR(100),
    underwriting_opinion TEXT,
    surcharge_condition VARCHAR(100),
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL,
    finalized_at TIMESTAMP
);

ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS review_id VARCHAR(50);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS application_id VARCHAR(50);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS underwriting_status VARCHAR(50);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS underwriting_type VARCHAR(50);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS auto_score REAL;
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS total_deduction INTEGER;
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS recommended_result VARCHAR(50);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS final_result VARCHAR(50);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS is_auto_review_available BOOLEAN;
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS is_coinsurance_recommended BOOLEAN;
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS itemized_scores TEXT;
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS underwriter_id VARCHAR(50);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS underwriter_name VARCHAR(100);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS department VARCHAR(100);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS underwriting_opinion TEXT;
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS surcharge_condition VARCHAR(100);
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE underwriting_review ADD COLUMN IF NOT EXISTS finalized_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_underwriting_review_application_id
    ON underwriting_review (application_id);

CREATE TABLE IF NOT EXISTS underwriting_history (
    history_id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    review_id VARCHAR(50),
    event_type VARCHAR(50) NOT NULL,
    event_message TEXT NOT NULL,
    score REAL,
    result VARCHAR(50),
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE underwriting_history ADD COLUMN IF NOT EXISTS history_id VARCHAR(50);
ALTER TABLE underwriting_history ADD COLUMN IF NOT EXISTS application_id VARCHAR(50);
ALTER TABLE underwriting_history ADD COLUMN IF NOT EXISTS review_id VARCHAR(50);
ALTER TABLE underwriting_history ADD COLUMN IF NOT EXISTS event_type VARCHAR(50);
ALTER TABLE underwriting_history ADD COLUMN IF NOT EXISTS event_message TEXT;
ALTER TABLE underwriting_history ADD COLUMN IF NOT EXISTS score REAL;
ALTER TABLE underwriting_history ADD COLUMN IF NOT EXISTS result VARCHAR(50);
ALTER TABLE underwriting_history ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_underwriting_history_application_id
    ON underwriting_history (application_id);
