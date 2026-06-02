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

CREATE TABLE IF NOT EXISTS contract (
    policy_number VARCHAR(50) PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    contract_status VARCHAR(50) NOT NULL,
    payment_cycle VARCHAR(50) NOT NULL,
    premium_amount NUMERIC(15,2) NOT NULL,
    installment_count INTEGER NOT NULL,
    has_unpaid_premium BOOLEAN NOT NULL,
    contract_start_date DATE NOT NULL,
    contract_end_date DATE NOT NULL,
    insured_name VARCHAR(100) NOT NULL,
    insured_rrn VARCHAR(20) NOT NULL,
    insured_contact VARCHAR(50) NOT NULL,
    account_number VARCHAR(50),
    account_bank VARCHAR(50),
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE contract ADD COLUMN IF NOT EXISTS product_code VARCHAR(50);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS contract_status VARCHAR(50);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS payment_cycle VARCHAR(50);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS premium_amount NUMERIC(15,2);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS installment_count INTEGER;
ALTER TABLE contract ADD COLUMN IF NOT EXISTS has_unpaid_premium BOOLEAN;
ALTER TABLE contract ADD COLUMN IF NOT EXISTS contract_start_date DATE;
ALTER TABLE contract ADD COLUMN IF NOT EXISTS contract_end_date DATE;
ALTER TABLE contract ADD COLUMN IF NOT EXISTS insured_name VARCHAR(100);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS insured_rrn VARCHAR(20);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS insured_contact VARCHAR(50);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS account_number VARCHAR(50);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS account_bank VARCHAR(50);
ALTER TABLE contract ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

INSERT INTO contract (
    policy_number, product_code, contract_status, payment_cycle,
    premium_amount, installment_count, has_unpaid_premium,
    contract_start_date, contract_end_date,
    insured_name, insured_rrn, insured_contact,
    account_number, account_bank, created_at
) VALUES
    ('POL-2024-000001', 'PRD-AUTO-001', 'ACTIVE', 'MONTHLY',
     120000.00, 12, FALSE,
     '2024-01-15', '2027-01-15',
     '홍길동', '900101-1234567', '010-1111-2222',
     '110-234-567890', 'SHINHAN', '2024-01-15 10:00:00'),
    ('POL-2023-000099', 'PRD-LIFE-002', 'ACTIVE', 'ANNUALLY',
     480000.00, 1, FALSE,
     '2023-06-01', '2026-06-01',
     '김영희', '850515-2345678', '010-3333-4444',
     '352-1122-334455', 'NH', '2023-06-01 09:30:00'),
    ('POL-2022-000050', 'PRD-FIRE-003', 'EXPIRED', 'QUARTERLY',
     200000.00, 4, FALSE,
     '2022-01-01', '2025-01-01',
     '박철수', '770808-1456789', '010-5555-6666',
     '301-9988-776655', 'KB', '2022-01-01 14:20:00')
ON CONFLICT (policy_number) DO NOTHING;

CREATE TABLE IF NOT EXISTS payout (
    payout_id VARCHAR(50) PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL,
    calculation_basis VARCHAR(50) NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    paid_premium_amount NUMERIC(15,2) NOT NULL,
    refund_rate NUMERIC(7,4) NOT NULL,
    calculated_amount NUMERIC(15,2) NOT NULL,
    deduction_item VARCHAR(255),
    deduction_amount NUMERIC(15,2) NOT NULL,
    final_payment_amount NUMERIC(15,2) NOT NULL,
    processor VARCHAR(50),
    payout_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP,
    paid_at TIMESTAMP,
    cancelled_at TIMESTAMP
);

ALTER TABLE payout ADD COLUMN IF NOT EXISTS policy_number VARCHAR(50);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS calculation_basis VARCHAR(50);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS payment_type VARCHAR(50);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS paid_premium_amount NUMERIC(15,2);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS refund_rate NUMERIC(7,4);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS calculated_amount NUMERIC(15,2);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS deduction_item VARCHAR(255);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS deduction_amount NUMERIC(15,2);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS final_payment_amount NUMERIC(15,2);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS processor VARCHAR(50);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS payout_status VARCHAR(50);
ALTER TABLE payout ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE payout ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP;
ALTER TABLE payout ADD COLUMN IF NOT EXISTS paid_at TIMESTAMP;
ALTER TABLE payout ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_payout_policy_number
    ON payout (policy_number);

CREATE TABLE IF NOT EXISTS payment_collection (
    collection_id VARCHAR(50) PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL,
    installment_no INTEGER NOT NULL,
    due_date DATE NOT NULL,
    planned_amount NUMERIC(15,2) NOT NULL,
    collected_amount NUMERIC(15,2) NOT NULL,
    unpaid_amount NUMERIC(15,2) NOT NULL,
    late_fee NUMERIC(15,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    processing_result VARCHAR(50) NOT NULL,
    collected_at TIMESTAMP NOT NULL,
    transfer_type VARCHAR(50),
    transferred_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS policy_number VARCHAR(50);
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS installment_no INTEGER;
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS due_date DATE;
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS planned_amount NUMERIC(15,2);
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS collected_amount NUMERIC(15,2);
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS unpaid_amount NUMERIC(15,2);
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS late_fee NUMERIC(15,2);
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS payment_method VARCHAR(50);
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS processing_result VARCHAR(50);
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS collected_at TIMESTAMP;
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS transfer_type VARCHAR(50);
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS transferred_at TIMESTAMP;
ALTER TABLE payment_collection ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_payment_collection_policy_number
    ON payment_collection (policy_number);

INSERT INTO contract (
    policy_number, product_code, contract_status, payment_cycle,
    premium_amount, installment_count, has_unpaid_premium,
    contract_start_date, contract_end_date,
    insured_name, insured_rrn, insured_contact,
    account_number, account_bank, created_at
) VALUES
    ('POL-2024-000111', 'PRD-AUTO-001', 'SUSPENDED', 'MONTHLY',
     150000.00, 12, TRUE,
     '2024-03-01', '2027-03-01',
     '이수영', '880920-2987654', '010-7777-8888',
     '110-555-112233', 'SHINHAN', '2024-03-01 11:00:00')
ON CONFLICT (policy_number) DO NOTHING;

CREATE TABLE IF NOT EXISTS reinstatement (
    reinstatement_id VARCHAR(50) PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL,
    reinstatement_reason VARCHAR(50) NOT NULL,
    desired_date DATE NOT NULL,
    has_health_changed BOOLEAN NOT NULL,
    last_paid_date DATE,
    unpaid_installment_count INTEGER NOT NULL,
    premium_per_installment NUMERIC(15,2) NOT NULL,
    unpaid_premium NUMERIC(15,2) NOT NULL,
    reinstatement_status VARCHAR(50) NOT NULL,
    applied_at TIMESTAMP NOT NULL,
    unpaid_settled_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP
);

ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS policy_number VARCHAR(50);
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS reinstatement_reason VARCHAR(50);
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS desired_date DATE;
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS has_health_changed BOOLEAN;
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS last_paid_date DATE;
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS unpaid_installment_count INTEGER;
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS premium_per_installment NUMERIC(15,2);
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS unpaid_premium NUMERIC(15,2);
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS reinstatement_status VARCHAR(50);
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS applied_at TIMESTAMP;
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS unpaid_settled_at TIMESTAMP;
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;
ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_reinstatement_policy_number
    ON reinstatement (policy_number);

CREATE UNIQUE INDEX IF NOT EXISTS idx_reinstatement_active_policy
    ON reinstatement (policy_number)
    WHERE reinstatement_status IN ('APPLIED', 'UNPAID_SETTLED');

ALTER TABLE reinstatement ADD COLUMN IF NOT EXISTS underwriting_request_id VARCHAR(50);

CREATE TABLE IF NOT EXISTS underwriting_request (
    request_id VARCHAR(50) PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL,
    request_reason VARCHAR(50) NOT NULL,
    source_id VARCHAR(50) NOT NULL,
    underwriting_type VARCHAR(50),
    request_status VARCHAR(50) NOT NULL,
    underwriting_result VARCHAR(50),
    rejection_reason VARCHAR(50),
    surcharge_condition VARCHAR(50),
    requested_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP
);

ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS policy_number VARCHAR(50);
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS request_reason VARCHAR(50);
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS source_id VARCHAR(50);
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS underwriting_type VARCHAR(50);
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS request_status VARCHAR(50);
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS underwriting_result VARCHAR(50);
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS rejection_reason VARCHAR(50);
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS surcharge_condition VARCHAR(50);
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS requested_at TIMESTAMP;
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;
ALTER TABLE underwriting_request ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_underwriting_request_policy_number
    ON underwriting_request (policy_number);
CREATE INDEX IF NOT EXISTS idx_underwriting_request_source_id
    ON underwriting_request (source_id);

CREATE TABLE IF NOT EXISTS endorsement (
    endorsement_id VARCHAR(50) PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL,
    endorsement_type VARCHAR(50) NOT NULL,
    change_reason VARCHAR(50) NOT NULL,
    previous_content TEXT NOT NULL,
    new_content TEXT NOT NULL,
    endorsement_status VARCHAR(50) NOT NULL,
    underwriting_request_id VARCHAR(50),
    applied_at TIMESTAMP NOT NULL,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    cancelled_at TIMESTAMP
);

ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS policy_number VARCHAR(50);
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS endorsement_type VARCHAR(50);
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS change_reason VARCHAR(50);
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS previous_content TEXT;
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS new_content TEXT;
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS endorsement_status VARCHAR(50);
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS underwriting_request_id VARCHAR(50);
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS applied_at TIMESTAMP;
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP;
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS rejected_at TIMESTAMP;
ALTER TABLE endorsement ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_endorsement_policy_number
    ON endorsement (policy_number);

CREATE UNIQUE INDEX IF NOT EXISTS idx_endorsement_active_policy
    ON endorsement (policy_number)
    WHERE endorsement_status = 'APPLIED';
