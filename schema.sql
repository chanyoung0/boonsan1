-- =====================================================
-- 신동아화재 보험 관리 시스템 DDL
-- PostgreSQL 기준
-- =====================================================

-- =====================================================
-- 1. 보험청약 심사 (UnderwritingDBO)
-- =====================================================
CREATE TABLE underwriting (
    underwriting_id     VARCHAR(50)     NOT NULL,
    underwriter_emp_no  VARCHAR(50),
    underwriter_name    VARCHAR(100),
    underwriter_dept    VARCHAR(100),
    total_score         REAL,
    underwriting_status VARCHAR(30),    -- PENDING | IN_PROGRESS | COMPLETED | APPROVED | REJECTED
    underwriting_type   VARCHAR(30),    -- AUTO | DIAGNOSIS | SPECIAL | GENERAL | IMAGE | FITNESS
    underwriting_result VARCHAR(30),    -- APPROVED | SURCHARGE | REJECTED
    rejection_reason    TEXT,
    underwritten_at     TIMESTAMP,
    CONSTRAINT pk_underwriting PRIMARY KEY (underwriting_id)
);

-- =====================================================
-- 2. 청약 (InsuranceApplicationDBO)
-- =====================================================
CREATE TABLE insurance_application (
    application_id      VARCHAR(50)     NOT NULL,
    policy_number       VARCHAR(50),
    application_status  VARCHAR(30),    -- PENDING | APPROVED | REJECTED | CANCELLED
    applied_condition   VARCHAR(200),
    applied_at          TIMESTAMP,
    CONSTRAINT pk_insurance_application PRIMARY KEY (application_id)
);


-- =====================================================
-- 3. 배서 (EndorsementDBO)
-- =====================================================
CREATE TABLE endorsement (
    endorsement_id      VARCHAR(50)     NOT NULL,
    policy_number       VARCHAR(50)     NOT NULL,
    endorsement_type    VARCHAR(50),    -- COVERAGE_CHANGE | PREMIUM_CHANGE | SPECIAL_CONTRACT_CHANGE | BENEFICIARY_CHANGE
    previous_content    TEXT,
    new_content         TEXT,
    change_reason       TEXT,
    underwriting_result VARCHAR(30),    -- APPROVED | SURCHARGE | REJECTED
    applied_at          TIMESTAMP,
    processed_at        TIMESTAMP,
    CONSTRAINT pk_endorsement PRIMARY KEY (endorsement_id)
);

-- =====================================================
-- 4. 부활 (ReinstatementDBO)
-- =====================================================
CREATE TABLE reinstatement (
    reinstatement_id    VARCHAR(50)     NOT NULL,
    policy_number       VARCHAR(50)     NOT NULL,
    underwriting_result VARCHAR(30),    -- APPROVED | SURCHARGE | REJECTED
    reinstatement_status VARCHAR(30),   -- PENDING | APPROVED | REJECTED
    applied_at          TIMESTAMP,
    processed_at        TIMESTAMP,
    CONSTRAINT pk_reinstatement PRIMARY KEY (reinstatement_id)
);

-- =====================================================
-- 5. 분납/수금 (PaymentCollectionDBO)
-- =====================================================
CREATE TABLE payment_collection (
    collection_id               VARCHAR(50)     NOT NULL,
    policy_number               VARCHAR(50)     NOT NULL,
    due_date                    DATE,
    collected_amount            NUMERIC(15, 2),
    unpaid_amount               NUMERIC(15, 2),
    unpaid_installment_count    INTEGER,
    processing_result           VARCHAR(30),    -- PENDING | SUCCESS | PARTIAL | FAILED
    collected_at                TIMESTAMP,
    transfer_type               VARCHAR(30),    -- VISIT_COLLECTION | TRANSFER
    collection_status           VARCHAR(30),    -- CREATED | COLLECTED | UNPAID | TRANSFERRED
    CONSTRAINT pk_payment_collection PRIMARY KEY (collection_id)
);

-- =====================================================
-- 6. 만기계약 (MaturityNoticeDBO)
-- =====================================================
CREATE TABLE maturity_notice (
    notice_id           VARCHAR(50)     NOT NULL,
    delivery_method     VARCHAR(30),    -- EMAIL | SMS | MAIL | PUSH_NOTIFICATION
    sent_at             TIMESTAMP,
    checked_at          TIMESTAMP,
    renewal_intention   VARCHAR(10),    -- YES | NO | NULL(회신없음)
    CONSTRAINT pk_maturity_notice PRIMARY KEY (notice_id)
);

-- =====================================================
-- 7. 사고 접수 (AccidentReportDBO)
-- =====================================================
CREATE TABLE accident_report (
    report_no                   VARCHAR(50)     NOT NULL,
    policy_number               VARCHAR(50)     NOT NULL,
    accident_description        TEXT,
    damage_details              TEXT,
    accident_status             VARCHAR(50),    -- RECEIVED | DOCUMENT_PENDING | INVESTIGATION_REQUIRED | REJECTED
    document_submission_status  VARCHAR(30),    -- PENDING | SUBMITTED
    accident_at_text            VARCHAR(30),
    created_at                  TIMESTAMP,
    CONSTRAINT pk_accident_report PRIMARY KEY (report_no)
);

-- =====================================================
-- 8. 손해조사 (DamageInvestigationDBO)
-- =====================================================
CREATE TABLE damage_investigation (
    investigation_id    VARCHAR(50)     NOT NULL,
    report_no           VARCHAR(50)     NOT NULL,
    adjuster_id         VARCHAR(50),
    medical_expense     NUMERIC(15, 2),
    lost_income         NUMERIC(15, 2),
    repair_cost         NUMERIC(15, 2),
    settlement_amount   NUMERIC(15, 2),
    fault_ratio         REAL,
    investigation_status VARCHAR(30),   -- PENDING | APPROVED | REJECTED
    investigation_at    TIMESTAMP,
    CONSTRAINT pk_damage_investigation PRIMARY KEY (investigation_id)
);

-- =====================================================
-- 9. 상품 개발 — 보험상품 (InsuranceDBO)
-- =====================================================
CREATE TABLE insurance (
    product_code        VARCHAR(50)     NOT NULL,
    product_type        VARCHAR(20),    -- AUTO | FIRE | MARINE
    insurance_period    VARCHAR(50),
    insured_amount      NUMERIC(20, 2),
    premium             NUMERIC(15, 2),
    maturity_refund     NUMERIC(15, 2),
    driver_age          INTEGER,        -- AUTO 전용
    vehicle_type        VARCHAR(100),   -- AUTO 전용
    building_type       VARCHAR(100),   -- FIRE 전용
    location            VARCHAR(200),   -- FIRE 전용
    vessel_type         VARCHAR(100),   -- MARINE 전용
    shipping_route      VARCHAR(200),   -- MARINE 전용
    CONSTRAINT pk_insurance PRIMARY KEY (product_code)
);

-- =====================================================
-- 10. 상품 개발 — 인가요청 (AuthorizationDBO)
-- =====================================================
CREATE TABLE authorization_request (
    request_id              VARCHAR(50)     NOT NULL,
    product_code            VARCHAR(50),
    request_reason          TEXT,
    submission_agency_name  VARCHAR(200),
    requested_at            TIMESTAMP,
    approved_at             TIMESTAMP,
    is_approved             BOOLEAN         DEFAULT FALSE,
    CONSTRAINT pk_authorization_request PRIMARY KEY (request_id)
);

-- =====================================================
-- 11. 제지급금 (PayoutDBO)
-- =====================================================
CREATE TABLE payout (
    payout_id               VARCHAR(50)     NOT NULL,
    policy_number           VARCHAR(50)     NOT NULL,
    processor               VARCHAR(100),
    payment_type            VARCHAR(30),    -- LUMP_SUM | INSTALLMENT | ANNUITY
    calculation_basis       VARCHAR(50),    -- MATURITY_REFUND | DEATH_BENEFIT | DISABILITY_BENEFIT 등
    calculated_amount       NUMERIC(15, 2),
    deduction_item          VARCHAR(200),
    final_payment_amount    NUMERIC(15, 2),
    approved_at             TIMESTAMP,
    paid_at                 TIMESTAMP,
    payout_status           VARCHAR(30),    -- REGISTERED | APPROVED | PAID | CANCELLED
    CONSTRAINT pk_payout PRIMARY KEY (payout_id)
);

-- =====================================================
-- 12. 보상평가 (CompensationEvaluationDBO)
-- =====================================================
CREATE TABLE compensation_evaluation (
    evaluation_id           VARCHAR(50)     NOT NULL,
    evaluation_month        INTEGER,
    evaluation_status       VARCHAR(30),    -- IN_PROGRESS | COMPLETED | CLOSED
    evaluation_result       VARCHAR(30),    -- PASS | FAIL | CONDITIONAL 등
    submission_agency_name  VARCHAR(200),
    damage_amount           NUMERIC(15, 2),
    damage_analysis_result  TEXT,
    compensation_statistics TEXT,
    CONSTRAINT pk_compensation_evaluation PRIMARY KEY (evaluation_id)
);

-- =====================================================
-- 13. 협력업체 (PartnerDBO)
-- =====================================================
CREATE TABLE partner (
    id                  VARCHAR(50)     NOT NULL,
    partner_name        VARCHAR(200)    NOT NULL,
    partner_type        VARCHAR(100),
    contact             VARCHAR(100),
    responsibility      VARCHAR(200),
    evaluation_grade    VARCHAR(20),    -- EXCELLENT | GOOD | AVERAGE | POOR
    CONSTRAINT pk_partner PRIMARY KEY (id)
);

-- =====================================================
-- 14. 계좌 (AccountDBO) — Unit 1
-- =====================================================
CREATE TABLE account (
    account_number  VARCHAR(50)     NOT NULL,
    account_holder  VARCHAR(100),
    bank_name       VARCHAR(30),    -- KB | SHINHAN | WOORI | HANA | IBK | NH | KAKAO | TOSS
    account_type    VARCHAR(30),    -- SAVINGS | CHECKING | AUTO_TRANSFER
    balance         NUMERIC(15, 2),
    CONSTRAINT pk_account PRIMARY KEY (account_number)
);

-- =====================================================
-- 15. 피보험자 (InsuredPersonDBO) — Unit 1
-- =====================================================
CREATE TABLE insured_person (
    resident_registration_number    VARCHAR(20) NOT NULL,
    name                            VARCHAR(100),
    contact                         VARCHAR(50),
    account_number                  VARCHAR(50),
    CONSTRAINT pk_insured_person PRIMARY KEY (resident_registration_number),
    CONSTRAINT fk_insured_person_account FOREIGN KEY (account_number)
        REFERENCES account(account_number)
);

-- =====================================================
-- 16. 심사 이력 (UnderwritingHistoryDBO) — Unit 2
-- =====================================================
CREATE TABLE underwriting_history (
    history_id                      VARCHAR(50) NOT NULL,
    resident_registration_number    VARCHAR(20),     -- FK to insured_person
    name                            VARCHAR(100),
    age                             INTEGER,
    gender                          VARCHAR(10),     -- MALE | FEMALE
    occupation                      VARCHAR(100),
    annual_income                   NUMERIC(15, 2),
    inquired_at                     TIMESTAMP,
    vehicle_model                   VARCHAR(100),
    vehicle_number                  VARCHAR(50),
    bmi                             VARCHAR(20),
    past_medical_history            TEXT,
    is_medicated                    BOOLEAN,
    surgery_history                 TEXT,
    family_history                  TEXT,
    is_smoker                       BOOLEAN,
    alcohol_consumption             VARCHAR(50),
    CONSTRAINT pk_underwriting_history PRIMARY KEY (history_id),
    CONSTRAINT fk_uw_history_insured_person FOREIGN KEY (resident_registration_number)
        REFERENCES insured_person(resident_registration_number)
);

-- =====================================================
-- 17. 사고 이력 (AccidentHistoryDBO) — Unit 2
-- =====================================================
CREATE TABLE accident_history (
    receipt_number          VARCHAR(50) NOT NULL,
    history_id              VARCHAR(50),     -- FK to underwriting_history (N:1)
    accident_type           VARCHAR(30),     -- AccidentType enum
    location                VARCHAR(200),
    occurred_at             TIMESTAMP,
    received_at             TIMESTAMP,
    claimed_amount          NUMERIC(15, 2),
    recognized_amount       NUMERIC(15, 2),
    diagnosis_code          VARCHAR(50),
    diagnosis_name          VARCHAR(200),
    treatment_details       TEXT,
    hospitalization_period  TIMESTAMP,
    has_surgery             BOOLEAN,
    paid_at                 TIMESTAMP,
    CONSTRAINT pk_accident_history PRIMARY KEY (receipt_number),
    CONSTRAINT fk_accident_history_uw_history FOREIGN KEY (history_id)
        REFERENCES underwriting_history(history_id)
);

-- =====================================================
-- 18. 심사 요청 (UnderwritingRequestDBO) — Unit 3
-- =====================================================
CREATE TABLE underwriting_request (
    request_id              VARCHAR(50) NOT NULL,
    underwriting_id         VARCHAR(50),                -- nullable FK to underwriting
    applied_at              TIMESTAMP,
    applied_id              TIMESTAMP,
    rejection_reason        VARCHAR(50),
    request_reason          VARCHAR(50),                -- ENDORSEMENT | REINSTATEMENT
    request_status          VARCHAR(30),                -- PENDING | COMPLETED
    surcharge_condition     VARCHAR(50),
    underwriting_result     VARCHAR(30),                -- APPROVED | SURCHARGE | REJECTED
    underwriting_type       VARCHAR(30),                -- GENERAL | AUTO 등
    CONSTRAINT pk_underwriting_request PRIMARY KEY (request_id),
    CONSTRAINT fk_uw_request_uw FOREIGN KEY (underwriting_id)
        REFERENCES underwriting(underwriting_id)
);

-- =====================================================
-- 19. 심사 결과 (UnderwritingResultDBO) — Unit 3
-- =====================================================
CREATE TABLE underwriting_result (
    result_id               VARCHAR(50) NOT NULL,
    underwriting_id         VARCHAR(50),                -- nullable FK to underwriting
    confirmed_at            TIMESTAMP,
    rejection_reason        TEXT,
    surcharge_condition     VARCHAR(50),
    underwriting_result     VARCHAR(30),                -- APPROVED | SURCHARGE | REJECTED
    CONSTRAINT pk_underwriting_result PRIMARY KEY (result_id),
    CONSTRAINT fk_uw_result_uw FOREIGN KEY (underwriting_id)
        REFERENCES underwriting(underwriting_id)
);

-- =====================================================
-- 20. 담당자 (ManagerDBO) — Unit 4
-- =====================================================
CREATE TABLE manager (
    employee_no     VARCHAR(50) NOT NULL,
    name            VARCHAR(100),
    department      VARCHAR(100),
    CONSTRAINT pk_manager PRIMARY KEY (employee_no)
);

INSERT INTO manager (employee_no, name, department) VALUES ('M001', '김상우', '계약관리1팀') ON CONFLICT (employee_no) DO NOTHING;
INSERT INTO manager (employee_no, name, department) VALUES ('M002', '박지영', '수금이관팀') ON CONFLICT (employee_no) DO NOTHING;
INSERT INTO manager (employee_no, name, department) VALUES ('M003', '이태준', '방문수금팀') ON CONFLICT (employee_no) DO NOTHING;

-- =====================================================
-- 21. 이관 (TransferDBO) — Unit 5
-- =====================================================
CREATE TABLE transfer (
    transfer_id             VARCHAR(50) NOT NULL,
    collection_id           VARCHAR(50),     -- FK to payment_collection
    manager_employee_no     VARCHAR(50),     -- FK to manager
    transfer_type           VARCHAR(30),     -- VISIT_COLLECTION | CANCELLATION | DEPARTMENT_CHANGE
    transferred_at          TIMESTAMP,
    CONSTRAINT pk_transfer PRIMARY KEY (transfer_id),
    CONSTRAINT fk_transfer_collection FOREIGN KEY (collection_id)
        REFERENCES payment_collection(collection_id),
    CONSTRAINT fk_transfer_manager FOREIGN KEY (manager_employee_no)
        REFERENCES manager(employee_no)
);

-- =====================================================
-- 22. 미납 안내 (UnpaidNoticeDBO) — Unit 5
-- =====================================================
CREATE TABLE unpaid_notice (
    notice_id           VARCHAR(50) NOT NULL,
    collection_id       VARCHAR(50),     -- FK to payment_collection
    unpaid_amount       NUMERIC(15, 2),
    due_date            TIMESTAMP,
    payment_method      VARCHAR(30),     -- AUTO_TRANSFER | BANK_TRANSFER | CREDIT_CARD | VISIT_COLLECTION
    sent_at             TIMESTAMP,
    CONSTRAINT pk_unpaid_notice PRIMARY KEY (notice_id),
    CONSTRAINT fk_unpaid_notice_collection FOREIGN KEY (collection_id)
        REFERENCES payment_collection(collection_id)
);

-- =====================================================
-- 23-1. 구상 (SubrogationDBO) — Unit 7
-- =====================================================
CREATE TABLE subrogation (
    subrogation_id      VARCHAR(50) NOT NULL,
    subrogation_status  VARCHAR(30),    -- PENDING | IN_PROGRESS | COMPLETED
    offender_name       VARCHAR(100),
    offender_contact    VARCHAR(50),
    fault_ratio         REAL,
    payment_amount      NUMERIC(15, 2),
    deposit_account     VARCHAR(100),
    payment_deadline    TIMESTAMP,
    CONSTRAINT pk_subrogation PRIMARY KEY (subrogation_id)
);

-- =====================================================
-- 23. 문서 (DocumentDBO) — Unit 6, 단일 테이블 + discriminator
-- =====================================================
CREATE TABLE document (
    document_id                 VARCHAR(50) NOT NULL,
    document_type               VARCHAR(30),    -- ACCIDENT | PAYMENT_APPROVAL (discriminator)
    status                      VARCHAR(30),    -- DocumentStatus enum
    created_at                  TIMESTAMP,
    -- AccidentDocument 전용 (nullable)
    check_due_date              TIMESTAMP,
    document_name               VARCHAR(50),    -- DocumentName enum
    accident_document_type      VARCHAR(50),    -- DocumentType enum
    submission_status           VARCHAR(30),    -- SubmissionStatus enum
    -- PaymentApprovalDocument 전용 (nullable)
    approval_status             VARCHAR(30),    -- ApprovalStatus enum
    approved_at                 TIMESTAMP,
    approver_employee_no        VARCHAR(50),
    damage_adequacy_opinion     TEXT,
    lost_income_amount          NUMERIC(15, 2),
    medical_expense_amount      NUMERIC(15, 2),
    remarks                     TEXT,
    repair_cost_amount          NUMERIC(15, 2),
    settlement_amount           NUMERIC(15, 2),
    CONSTRAINT pk_document PRIMARY KEY (document_id)
);
