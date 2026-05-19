-- 신동아화재 보험 관리 시스템 - PostgreSQL 스키마
-- docker-compose up 시 /docker-entrypoint-initdb.d 에서 자동 실행됨.
-- 모든 CREATE 는 IF NOT EXISTS 로 작성하여 재실행 시 안전.

-- 협력업체 (이미 운영 중)
CREATE TABLE IF NOT EXISTS partner (
    id                VARCHAR(50)  PRIMARY KEY,
    partner_name      VARCHAR(100) NOT NULL,
    partner_type      VARCHAR(50)  NOT NULL,
    contact           VARCHAR(50),
    responsibility    VARCHAR(100),
    evaluation_grade  VARCHAR(20)  NOT NULL
);

-- 보상평가 (이미 운영 중)
CREATE TABLE IF NOT EXISTS compensation_evaluation (
    evaluation_id            VARCHAR(50)    PRIMARY KEY,
    evaluation_month         INTEGER        NOT NULL,
    evaluation_status        VARCHAR(30)    NOT NULL,
    evaluation_result        VARCHAR(30),
    submission_agency_name   VARCHAR(100),
    damage_amount            NUMERIC(15, 2),
    damage_analysis_result   TEXT,
    compensation_statistics  TEXT
);

-- 사고 접수
CREATE TABLE IF NOT EXISTS accident_report (
    report_no             VARCHAR(50)  PRIMARY KEY,
    policy_number         VARCHAR(50),
    accident_description  TEXT,
    damage_details        TEXT,
    accident_status       VARCHAR(30),
    accident_at           TIMESTAMP,
    created_at            TIMESTAMP
);

-- 손해조사
CREATE TABLE IF NOT EXISTS damage_investigation (
    investigation_id   VARCHAR(50)    PRIMARY KEY,
    adjuster_id        VARCHAR(50),
    fault_ratio        REAL           NOT NULL DEFAULT 0,
    repair_cost        NUMERIC(15, 2),
    medical_expense    NUMERIC(15, 2),
    lost_income        NUMERIC(15, 2),
    settlement_amount  NUMERIC(15, 2),
    investigation_at   TIMESTAMP
);

-- 보험금 지급
CREATE TABLE IF NOT EXISTS insurance_payment (
    payment_id              VARCHAR(50)    PRIMARY KEY,
    payment_account         VARCHAR(200),
    processor_employee_no   VARCHAR(50),
    final_settlement_amount NUMERIC(15, 2),
    final_repair_cost       NUMERIC(15, 2),
    final_medical_expense   NUMERIC(15, 2),
    final_lost_income       NUMERIC(15, 2),
    retention_estimate      NUMERIC(15, 2),
    payment_status          VARCHAR(30)    NOT NULL,
    paid_at                 TIMESTAMP
);

-- 이의 신청
CREATE TABLE IF NOT EXISTS objection (
    objection_id              VARCHAR(50)    PRIMARY KEY,
    claimant_info             VARCHAR(200),
    objection_reason          TEXT,
    original_payment_details  TEXT,
    acceptance_status         VARCHAR(30)    NOT NULL,
    adjusted_amount           NUMERIC(15, 2),
    transfer_reason           TEXT
);

-- 외부 조사 위탁
CREATE TABLE IF NOT EXISTS outsource_request (
    request_id          VARCHAR(50)  PRIMARY KEY,
    request_status      VARCHAR(30)  NOT NULL,
    request_datetime    TIMESTAMP,
    result              TEXT,
    transferred_data    TEXT,
    partner_id          VARCHAR(50)  REFERENCES partner(id) ON DELETE SET NULL
);

-- 구상권
CREATE TABLE IF NOT EXISTS subrogation (
    subrogation_id       VARCHAR(50)    PRIMARY KEY,
    offender_name        VARCHAR(100),
    offender_contact     VARCHAR(50),
    fault_ratio          REAL           NOT NULL DEFAULT 0,
    payment_amount       NUMERIC(15, 2),
    payment_deadline     TIMESTAMP,
    deposit_account      VARCHAR(200),
    subrogation_status   VARCHAR(30)    NOT NULL
);

-- ===========================================
-- 계약 영역
-- ===========================================

-- 계약 (Contract) — policyNumber 가 PK
CREATE TABLE IF NOT EXISTS contract (
    policy_number          VARCHAR(50)  PRIMARY KEY,
    contract_status        VARCHAR(30),
    payment_cycle          VARCHAR(30),
    has_unpaid_premium     BOOLEAN,
    installment_count      INTEGER      NOT NULL DEFAULT 0
);

-- 배서
CREATE TABLE IF NOT EXISTS endorsement (
    endorsement_id    VARCHAR(50)  PRIMARY KEY,
    policy_number     VARCHAR(50)  REFERENCES contract(policy_number) ON DELETE CASCADE,
    endorsement_type  VARCHAR(50),
    change_reason     VARCHAR(50),
    previous_content  TEXT,
    new_content       TEXT,
    applied_at        TIMESTAMP,
    processed_at      TIMESTAMP
);

-- 부활
CREATE TABLE IF NOT EXISTS reinstatement (
    reinstatement_id      VARCHAR(50)    PRIMARY KEY,
    policy_number         VARCHAR(50)    REFERENCES contract(policy_number) ON DELETE CASCADE,
    reinstatement_reason  VARCHAR(50),
    unpaid_premium        NUMERIC(15, 2),
    applied_at            TIMESTAMP,
    desired_date          TIMESTAMP,
    last_paid_date        DATE,
    has_health_changed    BOOLEAN        NOT NULL DEFAULT FALSE,
    processed_at          TIMESTAMP
);

-- 분납/수금
CREATE TABLE IF NOT EXISTS payment_collection (
    payment_collection_id     VARCHAR(50)    PRIMARY KEY,
    policy_number             VARCHAR(50)    REFERENCES contract(policy_number) ON DELETE CASCADE,
    due_date                  DATE,
    collected_amount          NUMERIC(15, 2),
    unpaid_amount             NUMERIC(15, 2),
    unpaid_installment_count  INTEGER        NOT NULL DEFAULT 0,
    processing_result         VARCHAR(30),
    collected_at              TIMESTAMP
);

-- 만기 안내
CREATE TABLE IF NOT EXISTS maturity_notice (
    maturity_notice_id  VARCHAR(50)  PRIMARY KEY,
    policy_number       VARCHAR(50)  REFERENCES contract(policy_number) ON DELETE CASCADE,
    delivery_method     VARCHAR(30),
    sent_at             TIMESTAMP,
    renewal_intention   BOOLEAN,
    checked_at          TIMESTAMP
);

-- 제지급금
CREATE TABLE IF NOT EXISTS payout (
    payout_id            VARCHAR(50)    PRIMARY KEY,
    policy_number        VARCHAR(50)    REFERENCES contract(policy_number) ON DELETE SET NULL,
    processor            VARCHAR(100),
    payment_type         VARCHAR(30),
    calculation_basis    VARCHAR(30),
    calculated_amount    NUMERIC(15, 2),
    final_payment_amount NUMERIC(15, 2),
    deduction_item       VARCHAR(200),
    approved_at          TIMESTAMP,
    paid_at              TIMESTAMP,
    cancelled            BOOLEAN        NOT NULL DEFAULT FALSE,
    rejection_reason     TEXT
);

-- 이관
CREATE TABLE IF NOT EXISTS transfer (
    transfer_id            VARCHAR(50)  PRIMARY KEY,
    payment_collection_id  VARCHAR(50)  REFERENCES payment_collection(payment_collection_id) ON DELETE CASCADE,
    transfer_type          VARCHAR(30),
    assignee_id            VARCHAR(50),
    transferred_at         TIMESTAMP
);

-- 미납 안내
CREATE TABLE IF NOT EXISTS unpaid_notice (
    unpaid_notice_id       VARCHAR(50)    PRIMARY KEY,
    payment_collection_id  VARCHAR(50)    REFERENCES payment_collection(payment_collection_id) ON DELETE CASCADE,
    unpaid_amount          NUMERIC(15, 2),
    due_date               TIMESTAMP,
    payment_method         VARCHAR(30),
    sent_at                TIMESTAMP
);

-- ===========================================
-- 사고 영역 (이어서)
-- ===========================================

-- ===========================================
-- 언더라이팅 영역
-- ===========================================

-- 보험 청약
CREATE TABLE IF NOT EXISTS insurance_application (
    application_id          VARCHAR(50)    PRIMARY KEY,
    product_code            VARCHAR(50),
    insured_person_info     TEXT,
    insured_amount          NUMERIC(15, 2),
    premium                 NUMERIC(15, 2),
    payment_cycle           VARCHAR(30),
    special_contract_type   VARCHAR(50),
    terms_version           VARCHAR(50),
    applied_condition       VARCHAR(100),
    application_status      VARCHAR(30),
    applied_at              TIMESTAMP
);

-- 언더라이팅
CREATE TABLE IF NOT EXISTS underwriting (
    underwriting_id            VARCHAR(50)  PRIMARY KEY,
    application_id             VARCHAR(50)  REFERENCES insurance_application(application_id) ON DELETE SET NULL,
    underwriter                VARCHAR(100),
    underwriting_type          VARCHAR(30),
    underwriting_status        VARCHAR(30),
    underwriting_item          VARCHAR(30),
    underwritten_at            TIMESTAMP,
    total_score                REAL         NOT NULL DEFAULT 0,
    itemized_scores            TEXT,
    deduction_reason           TEXT,
    underwriting_opinion       TEXT,
    is_coinsurance_recommended BOOLEAN      NOT NULL DEFAULT FALSE
);

-- 언더라이팅 결과
CREATE TABLE IF NOT EXISTS underwriting_result (
    result_id              VARCHAR(50)  PRIMARY KEY,
    underwriting_id        VARCHAR(50)  REFERENCES underwriting(underwriting_id) ON DELETE CASCADE,
    underwriting_result    VARCHAR(30),
    rejection_reason       TEXT,
    surcharge_condition    VARCHAR(50),
    confirmed_at           TIMESTAMP
);

-- 언더라이팅 요청 (배서/부활 등에서 사용)
CREATE TABLE IF NOT EXISTS underwriting_request (
    request_id            VARCHAR(50)  PRIMARY KEY,
    policy_number         VARCHAR(50),
    request_reason        VARCHAR(50),
    request_status        VARCHAR(30),
    underwriting_type     VARCHAR(30),
    underwriting_result   VARCHAR(30),
    surcharge_condition   VARCHAR(50),
    rejection_reason      VARCHAR(50),
    applied_at            TIMESTAMP
);

-- 언더라이팅 이력
CREATE TABLE IF NOT EXISTS underwriting_history (
    history_id                    VARCHAR(50)    PRIMARY KEY,
    insured_person_id             VARCHAR(50),
    name                          VARCHAR(100),
    age                           INTEGER        NOT NULL DEFAULT 0,
    gender                        VARCHAR(20),
    resident_registration_number  VARCHAR(20),
    occupation                    VARCHAR(100),
    annual_income                 NUMERIC(15, 2),
    bmi                           VARCHAR(20),
    past_medical_history          TEXT,
    is_medicated                  BOOLEAN        NOT NULL DEFAULT FALSE,
    surgery_history               TEXT,
    family_history                TEXT,
    is_smoker                     BOOLEAN        NOT NULL DEFAULT FALSE,
    alcohol_consumption           VARCHAR(50),
    vehicle_model                 VARCHAR(100),
    vehicle_number                VARCHAR(50),
    inquired_at                   TIMESTAMP
);

-- 공동보험
CREATE TABLE IF NOT EXISTS coinsurance (
    coinsurance_id        VARCHAR(50)    PRIMARY KEY,
    application_id        VARCHAR(50)    REFERENCES insurance_application(application_id) ON DELETE SET NULL,
    retained_share_rate   REAL           NOT NULL DEFAULT 0,
    retained_amount       NUMERIC(15, 2),
    received_at           TIMESTAMP
);

-- 공동보험사
CREATE TABLE IF NOT EXISTS coinsurer (
    coinsurer_id              VARCHAR(50)    PRIMARY KEY,
    coinsurance_id            VARCHAR(50)    REFERENCES coinsurance(coinsurance_id) ON DELETE CASCADE,
    company_name              VARCHAR(100),
    share_rate                REAL           NOT NULL DEFAULT 0,
    max_acceptable_share_rate REAL           NOT NULL DEFAULT 0,
    allocated_premium         NUMERIC(15, 2),
    retained_amount           NUMERIC(15, 2),
    is_approved               BOOLEAN        NOT NULL DEFAULT FALSE,
    rejection_reason          TEXT
);

-- 재보험
CREATE TABLE IF NOT EXISTS reinsurance (
    reinsurance_id            VARCHAR(50)    PRIMARY KEY,
    contract_id               VARCHAR(50),
    reinsurer_name            VARCHAR(100),
    reinsurance_method        VARCHAR(50),
    reinsurance_rate          REAL           NOT NULL DEFAULT 0,
    cession_rate              REAL           NOT NULL DEFAULT 0,
    retention_rate            REAL           NOT NULL DEFAULT 0,
    reinsurance_premium       NUMERIC(15, 2),
    settlement_amount         NUMERIC(15, 2),
    settlement_method         VARCHAR(50),
    expected_settlement_date  DATE,
    accounting_date           TIMESTAMP
);

-- ===========================================
-- 사고 영역 (이어서 — 사고 이력)
-- ===========================================

-- 사고 이력
CREATE TABLE IF NOT EXISTS accident_history (
    receipt_number          VARCHAR(50)    PRIMARY KEY,
    accident_type           VARCHAR(30),
    location                VARCHAR(200),
    occurred_at             TIMESTAMP,
    received_at             TIMESTAMP,
    claimed_amount          NUMERIC(15, 2),
    recognized_amount       NUMERIC(15, 2),
    diagnosis_code          VARCHAR(50),
    diagnosis_name          VARCHAR(200),
    treatment_details       TEXT,
    hospitalization_period  TIMESTAMP,
    has_surgery             BOOLEAN        NOT NULL DEFAULT FALSE,
    paid_at                 TIMESTAMP
);
