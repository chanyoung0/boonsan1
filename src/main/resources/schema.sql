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

CREATE TABLE IF NOT EXISTS credit_information_inquiry (
    inquiry_id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_identifier_masked VARCHAR(100),
    accident_history_exists BOOLEAN NOT NULL,
    other_insurance_contract_exists BOOLEAN NOT NULL,
    previous_claim_exists BOOLEAN NOT NULL,
    credit_risk_grade VARCHAR(50) NOT NULL,
    risk_flags TEXT NOT NULL,
    inquiry_status VARCHAR(50) NOT NULL,
    external_system_message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS inquiry_id VARCHAR(50);
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS application_id VARCHAR(50);
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS customer_name VARCHAR(100);
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS customer_identifier_masked VARCHAR(100);
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS accident_history_exists BOOLEAN;
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS other_insurance_contract_exists BOOLEAN;
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS previous_claim_exists BOOLEAN;
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS credit_risk_grade VARCHAR(50);
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS risk_flags TEXT;
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS inquiry_status VARCHAR(50);
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS external_system_message TEXT;
ALTER TABLE credit_information_inquiry ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_credit_information_inquiry_application_id
    ON credit_information_inquiry (application_id);

CREATE TABLE IF NOT EXISTS coinsurance_process (
    process_id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    coinsurer_name VARCHAR(100) NOT NULL,
    request_status VARCHAR(50) NOT NULL,
    result_status VARCHAR(50) NOT NULL,
    retained_amount NUMERIC(15,2),
    share_rate NUMERIC(7,2),
    is_manual_selected BOOLEAN NOT NULL,
    rejection_reason TEXT,
    external_system_message TEXT NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    result_registered_at TIMESTAMP,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS process_id VARCHAR(50);
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS application_id VARCHAR(50);
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS coinsurer_name VARCHAR(100);
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS request_status VARCHAR(50);
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS result_status VARCHAR(50);
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS retained_amount NUMERIC(15,2);
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS share_rate NUMERIC(7,2);
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS is_manual_selected BOOLEAN;
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS external_system_message TEXT;
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS requested_at TIMESTAMP;
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS result_registered_at TIMESTAMP;
ALTER TABLE coinsurance_process ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_coinsurance_process_application_id
    ON coinsurance_process (application_id);

CREATE TABLE IF NOT EXISTS reinsurance_process (
    process_id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    is_reinsurance_required BOOLEAN NOT NULL,
    reinsurance_reason TEXT NOT NULL,
    reinsurer_name VARCHAR(100) NOT NULL,
    request_status VARCHAR(50) NOT NULL,
    result_status VARCHAR(50) NOT NULL,
    retention_amount NUMERIC(15,2),
    cession_rate NUMERIC(7,2),
    rejection_reason TEXT,
    external_system_message TEXT NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    result_registered_at TIMESTAMP,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS process_id VARCHAR(50);
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS application_id VARCHAR(50);
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS is_reinsurance_required BOOLEAN;
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS reinsurance_reason TEXT;
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS reinsurer_name VARCHAR(100);
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS request_status VARCHAR(50);
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS result_status VARCHAR(50);
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS retention_amount NUMERIC(15,2);
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS cession_rate NUMERIC(7,2);
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS external_system_message TEXT;
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS requested_at TIMESTAMP;
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS result_registered_at TIMESTAMP;
ALTER TABLE reinsurance_process ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_reinsurance_process_application_id
    ON reinsurance_process (application_id);

CREATE TABLE IF NOT EXISTS insurance_policy_issue (
    issue_id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    policy_number VARCHAR(50) NOT NULL,
    issue_status VARCHAR(50) NOT NULL,
    final_result VARCHAR(50) NOT NULL,
    applied_condition TEXT,
    external_system_message TEXT NOT NULL,
    issued_at TIMESTAMP NOT NULL
);

ALTER TABLE insurance_policy_issue ADD COLUMN IF NOT EXISTS issue_id VARCHAR(50);
ALTER TABLE insurance_policy_issue ADD COLUMN IF NOT EXISTS application_id VARCHAR(50);
ALTER TABLE insurance_policy_issue ADD COLUMN IF NOT EXISTS policy_number VARCHAR(50);
ALTER TABLE insurance_policy_issue ADD COLUMN IF NOT EXISTS issue_status VARCHAR(50);
ALTER TABLE insurance_policy_issue ADD COLUMN IF NOT EXISTS final_result VARCHAR(50);
ALTER TABLE insurance_policy_issue ADD COLUMN IF NOT EXISTS applied_condition TEXT;
ALTER TABLE insurance_policy_issue ADD COLUMN IF NOT EXISTS external_system_message TEXT;
ALTER TABLE insurance_policy_issue ADD COLUMN IF NOT EXISTS issued_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_insurance_policy_issue_application_id
    ON insurance_policy_issue (application_id);

CREATE UNIQUE INDEX IF NOT EXISTS idx_insurance_policy_issue_policy_number
    ON insurance_policy_issue (policy_number);

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
     '계약테스트1', '900101-1******', '010-0000-0001',
     'TEST-ACCOUNT-0001', 'SHINHAN', '2024-01-15 10:00:00'),
    ('POL-2023-000099', 'PRD-LIFE-002', 'ACTIVE', 'ANNUALLY',
     480000.00, 1, FALSE,
     '2023-06-01', '2026-06-01',
     '계약테스트2', '850515-2******', '010-0000-0002',
     'TEST-ACCOUNT-0002', 'NH', '2023-06-01 09:30:00'),
    ('POL-2022-000050', 'PRD-FIRE-003', 'EXPIRED', 'QUARTERLY',
     200000.00, 4, FALSE,
     '2022-01-01', '2025-01-01',
     '계약테스트3', '770808-1******', '010-0000-0003',
     'TEST-ACCOUNT-0003', 'KB', '2022-01-01 14:20:00'),
    ('POL-2024-000111', 'PRD-AUTO-001', 'SUSPENDED', 'MONTHLY',
     150000.00, 12, TRUE,
     '2024-03-01', '2027-03-01',
     '계약테스트4', '880920-2******', '010-0000-0004',
     'TEST-ACCOUNT-0004', 'SHINHAN', '2024-03-01 11:00:00')
ON CONFLICT (policy_number) DO NOTHING;

CREATE TABLE IF NOT EXISTS contract_payout (
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

CREATE INDEX IF NOT EXISTS idx_contract_payout_policy_number
    ON contract_payout (policy_number);

CREATE TABLE IF NOT EXISTS contract_payment_collection (
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

CREATE INDEX IF NOT EXISTS idx_contract_payment_collection_policy_number
    ON contract_payment_collection (policy_number);

CREATE TABLE IF NOT EXISTS contract_reinstatement (
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
    underwriting_request_id VARCHAR(50),
    applied_at TIMESTAMP NOT NULL,
    unpaid_settled_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_contract_reinstatement_policy_number
    ON contract_reinstatement (policy_number);

CREATE UNIQUE INDEX IF NOT EXISTS idx_contract_reinstatement_active_policy
    ON contract_reinstatement (policy_number)
    WHERE reinstatement_status IN ('APPLIED', 'UNPAID_SETTLED');

CREATE TABLE IF NOT EXISTS contract_underwriting_request (
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

CREATE INDEX IF NOT EXISTS idx_contract_underwriting_request_policy_number
    ON contract_underwriting_request (policy_number);

CREATE INDEX IF NOT EXISTS idx_contract_underwriting_request_source_id
    ON contract_underwriting_request (source_id);

CREATE TABLE IF NOT EXISTS contract_endorsement (
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

CREATE INDEX IF NOT EXISTS idx_contract_endorsement_policy_number
    ON contract_endorsement (policy_number);

CREATE UNIQUE INDEX IF NOT EXISTS idx_contract_endorsement_active_policy
    ON contract_endorsement (policy_number)
    WHERE endorsement_status = 'APPLIED';
