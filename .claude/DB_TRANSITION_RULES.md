# Boonsan1 DB 전환 작업 규칙 및 진행 기준

## 0. 문서 목적

이 문서는 `boonsan1` 프로젝트에서 지금까지 확정한 DB 전환 규칙, 계층 구조, 작업 순서, 완료된 테이블, 남은 작업 범위, 팀원 분담 기준을 하나로 정리한 문서이다.

앞으로 DB 전환 작업을 진행할 때는 이 문서를 기준으로 한다.

---

## 1. 현재 프로젝트 기준

### 1.1 현재 작업 브랜치

- 저장소: `chanyoung0/boonsan1`
- 통합 브랜치: `Gihyeon_code_3rd`
- ChanYoung 작업 브랜치: `Gihyeon_code_3rd_ChanYoung`

### 1.2 현재 DBMS

- DBMS: PostgreSQL
- Database 이름: `boonsan`
- JDBC 방식: PostgreSQL JDBC Driver 직접 추가
- MyBatis: 아직 적용하지 않음
- Maven/Gradle: 현재 기준 사용하지 않음
- JDBC jar: `postgresql-42.7.11.jar`

### 1.3 현재까지 완료된 DB 전환 기능

| 기능 | 테이블 | 구조 |
|---|---|---|
| 보험청약 심사 | `underwriting` | `UnderwritingConsole → UnderwritingService → UnderwritingDBO → DBA` |
| 청약서 및 증권발행 | `insurance_application` | `UnderwritingConsole → UnderwritingService → InsuranceApplicationDBO → DBA` |
| 심사 결과 | `underwriting_result` | `UnderwritingConsole → UnderwritingService → UnderwritingResultDBO → DBA` |
| 심사 요청 | `underwriting_request` | `UnderwritingConsole → UnderwritingService → UnderwritingRequestDBO → DBA` |
| 심사 이력 | `underwriting_history` | `UnderwritingConsole → UnderwritingService → UnderwritingHistoryDBO → DBA` |
| 사고 이력 | `accident_history` | `UnderwritingService → AccidentHistoryDBO → DBA` |
| 배서 관리 | `endorsement` | `EndorsementConsole → EndorsementService → EndorsementDBO → DBA` |
| 부활 관리 | `reinstatement` | `ReinstatementConsole → ReinstatementService → ReinstatementDBO → DBA` |
| 분납/수금 관리 | `payment_collection` | `PaymentCollectionConsole → PaymentCollectionService → PaymentCollectionDBO → DBA` |
| 만기계약 관리 | `maturity_notice` | `MaturityContractConsole → MaturityContractService → MaturityNoticeDBO → DBA` |
| 사고 접수 | `accident_report` | `AccidentReportConsole → AccidentReportService → AccidentReportDBO → DBA` |
| 손해조사 | `damage_investigation` | `DamageInvestigationConsole → DamageInvestigationService → DamageInvestigationDBO → DBA` |
| 외부 위탁 | `outsource_request` | `DamageInvestigationConsole → DamageInvestigationService → OutsourceRequestDBO → DBA` |
| 보험금 지급 | `insurance_payment` | `DamageInvestigationConsole → DamageInvestigationService → InsurancePaymentDBO → DBA` |
| 이의 신청 | `objection` | `DamageInvestigationConsole → DamageInvestigationService → ObjectionDBO → DBA` |
| 구상 처리 | `subrogation` | `DamageInvestigationConsole → SubrogationService → SubrogationDBO → DBA` |
| 상품 개발 | `insurance`, `authorization_request` | `InsuranceProductConsole → InsuranceProductService → InsuranceDBO / AuthorizationDBO → DBA` |
| 제지급금 관리 | `payout` | `PayoutConsole → PayoutService → PayoutDBO → DBA` |
| 보상평가 관리 | `compensation_evaluation` | `CompensationEvaluationConsole → CompensationEvaluationService → CompensationEvaluationDBO → DBA` |
| 협력업체 관리 | `partner` | `PartnerConsole → PartnerService → PartnerDBO → DBA` |

> **총 21개 테이블, 21개 DBO 모두 실제 JDBC(PreparedStatement) 기반으로 완료.**

### 1.4 현재 패키지 구조

```
src
├── Main.java
├── common
├── console
│   ├── PartnerConsole.java          
│   ├── accident
│   ├── contract
│   ├── insurance
│   └── underwriting
├── service
│   ├── accident
│   ├── contract
│   ├── insurance
│   ├── partner
│   └── underwriting
├── model
│   ├── accident
│   ├── contract
│   ├── document
│   ├── insurance
│   ├── partner
│   ├── person
│   └── underwriting
├── db                               ← 모든 DBO 플랫 구조
└── enums                            ← 모든 enum 플랫 구조
```

---

## 2. DB 계층 구조 고정 기준

프로젝트의 DB 연동 흐름은 다음 구조로 고정한다.

```text
Console → Service → DBO → DBA → PostgreSQL DB
```

### 2.1 Console 역할

Console은 사용자와 직접 만나는 계층이다.

담당:
- 메뉴 출력
- 사용자 입력
- 결과 출력
- 오류 메시지 출력
- Service 호출

금지:
- DBO 직접 호출
- DBA 직접 호출
- SQL 작성
- DB Connection 생성
- ResultSet 처리
- PreparedStatement 처리

Console은 Service만 호출해야 한다.

---

### 2.2 Service 역할

Service는 유스케이스 흐름을 담당한다.

담당:
- 유스케이스 흐름 제어
- 도메인 객체 생성
- 도메인 객체 상태 변경 요청
- DB 저장/조회 요청을 DBO에 위임
- Console에 반환할 결과 정리
- 기존 public 메서드 시그니처 최대한 유지
- 모델에 없는 DB 관리 값은 필요 시 Service/DBO 계층에서 관리

금지:
- Connection 생성
- PreparedStatement 직접 작성
- ResultSet 직접 처리
- SQL 문자열 작성
- Console 출력 직접 담당 과다화

단, 기존 구조상 Console 출력 흐름과 맞추기 위한 간단한 결과 문자열/요약 반환은 허용한다.

---

### 2.3 DBO 역할

DBO는 `Database Operator`로 정의한다.

실질적으로 DAO 역할을 한다.

담당:
- SQL 작성
- CRUD 처리
- PreparedStatement 사용
- ResultSet mapping
- DB 컬럼 ↔ Model 필드 변환
- 모델에 없는 DB 보조값 조회/저장
- `findById`, `findAll`, `save`, `update`, `delete` 구현

금지:
- Console 출력
- 유스케이스 흐름 판단
- 메뉴 로직 처리
- 사용자 입력 처리
- 도메인 정책을 과하게 포함

DBO 이름은 유지한다.

새 `DAO` 패키지나 `Repository` 패키지는 만들지 않는다.

---

### 2.4 DBA 역할

DBA는 DB 접근 공통 기반 클래스다.

담당:
- `db.properties` 읽기
- PostgreSQL Connection 생성
- 공통 DB 연결 기반 제공
- 기존 출력용 `executeSelect`, `executeInsert`, `executeUpdate`, `executeDelete` 유지

주의:
- 기존 DBO들이 아직 `executeSelect/Insert/Update/Delete(String)`에 의존할 수 있으므로 기존 메서드를 삭제하지 않는다.
- 새 DB 전환 DBO는 `getConnection()` 기반으로 실제 JDBC CRUD를 구현한다.
- DBA에 특정 도메인 SQL을 넣지 않는다.

금지:
- 특정 도메인 SQL
- 도메인 객체 생성
- 유스케이스 판단

---

### 2.5 Model 역할

Model은 도메인 상태와 도메인 행위를 담당한다.

담당:
- 필드 보관
- 도메인 상태 변경
- 간단한 도메인 규칙 수행

금지:
- DBO 호출
- DBA 호출
- SQL 작성
- DB 저장 직접 수행
- Console 출력
- 사용자 입력 처리

중요:
- DB 전환을 위해 Model 필드를 함부로 추가하지 않는다.
- 모델에 없는 값은 가능하면 Service/DBO 계층에서 관리한다.
- 단, 반드시 도메인 필드로 필요한 경우에는 별도 승인 후 수정한다.

---

## 3. DB 설정 파일 기준

### 3.1 `db.properties`

각 팀원은 프로젝트 루트에 `db.properties`를 직접 생성한다.

예:

```properties
db.url=jdbc:postgresql://localhost:5432/boonsan
db.user=postgres
db.password=각자_비밀번호
```

주의:
- `db.properties`는 개인 로컬 설정 파일이다.
- GitHub에 올리면 안 된다.
- `.gitignore`에 반드시 포함한다.

---

### 3.2 `db.properties.example`

팀원 공유용 예시 파일이다.

예:

```properties
db.url=jdbc:postgresql://localhost:5432/boonsan
db.user=postgres
db.password=your_password
```

주의:
- 이 파일은 GitHub에 올려도 된다.
- 실제 비밀번호를 넣지 않는다.
- 팀원은 이 파일을 복사해서 `db.properties`로 만든 뒤 자기 비밀번호를 입력한다.

---

### 3.3 Git에 올리면 안 되는 파일

다음 파일은 절대 GitHub에 올리지 않는다.

```text
db.properties
bin/
*.jar
.git/
sources.txt
sources_ascii.txt
개인 로컬 설정 파일
```

특히 `postgresql-42.7.11.jar`는 `lib`에 두더라도 `.gitignore`의 `*.jar` 때문에 Git에 올리지 않는다.

---

## 4. 팀원 로컬 실행 준비 절차

각 팀원은 다음 준비를 해야 한다.

1. PostgreSQL 설치
2. `boonsan` database 생성
3. `schema.sql` 전체 실행하여 테이블 생성
4. PostgreSQL JDBC Driver 다운로드
5. `lib/postgresql-42.7.11.jar` 배치
6. Eclipse Build Path에 jar 추가
7. `db.properties.example` 복사
8. `db.properties` 생성 후 자기 PostgreSQL 비밀번호 입력
9. `Main.java` 실행
10. 각 메뉴 기능 테스트

주의:
- Java 코드를 실행한다고 PostgreSQL 테이블이 자동 생성되지 않는다.
- 테이블은 `schema.sql`을 pgAdmin Query Tool 등에서 실행하여 생성해야 한다.

---

## 5. CRUD 반환형 기준

DBO의 기본 CRUD 반환형은 다음 기준으로 통일한다.

| 메서드 | 반환형 | 기준 |
|---|---|---|
| `findById` 계열 | 객체 또는 `null` | 없으면 `null` |
| `findAll` | `List<T>` | 없으면 빈 리스트 |
| `save` / `insert` | `boolean` | `affectedRows > 0` |
| `update` | `boolean` | `affectedRows > 0` |
| `delete` | `boolean` | `affectedRows > 0` |

MyBatis를 나중에 적용하면 Mapper의 `insert/update/delete`는 `int` 영향 행 수를 반환할 수 있다.
하지만 현재 DBO 구조에서는 DBO 내부에서 `int > 0`을 `boolean`으로 변환해 Service에 전달하는 방식을 우선한다.

---

## 6. 테이블 설계 공통 기준

### 6.1 테이블명

테이블명은 소문자와 언더스코어를 사용한다.

예:

```text
partner
compensation_evaluation
authorization_request
payment_collection
accident_report
```

PostgreSQL 예약어 충돌 가능성이 있는 이름은 피한다.

예:
- `authorization`은 사용하지 않는다.
- 대신 `authorization_request`를 사용한다.

---

### 6.2 모델에 없는 값 관리 기준

DB에는 필요하지만 Model에 없는 값이 있을 수 있다.

예:
- `payout_id`, `policy_number`, `payout_status`
- `collection_id`, `collection_status`, `transfer_type`
- `accident_at_text`, `document_submission_status`

이 경우 기본 원칙은 다음과 같다.

1. Model에 새 필드를 바로 추가하지 않는다.
2. Service/DBO 계층에서 보조 인자로 관리한다.
3. 테이블 컬럼으로는 저장한다.
4. Console 출력이 필요하면 Service 보조 메서드로 조회한다.

예:

```java
PayoutDBO.save(payout, payoutId, policyNumber, payoutStatus);
AuthorizationDBO.save(authorization, productCode);
AccidentReportDBO.save(report, policyNumber, accidentStatus, documentSubmissionStatus, accidentAtText);
PaymentCollectionDBO.save(collection, collectionId, policyNumber, collectionStatus, transferType);
```

---

### 6.3 외래키 기준

아직 기반 테이블이 DB 전환되지 않았거나 관계 설계가 불확실하면 FK를 걸지 않는다.

예:
- `payout.policy_number`는 `contract` 테이블 전환 전이므로 FK 없음
- `payment_collection.policy_number`도 FK 없음
- `accident_report.policy_number`도 FK 없음

단, 이미 테이블이 확정된 경우에는 FK를 사용할 수 있다.

예:
- `outsource_request.investigation_id → damage_investigation.investigation_id`
- `insurance_payment.investigation_id → damage_investigation.investigation_id`
- `objection.payment_id → insurance_payment.payment_id`
- `subrogation.payment_id → insurance_payment.payment_id`
- `underwriting_result.underwriting_id → underwriting.underwriting_id`
- `underwriting_history.underwriting_id → underwriting.underwriting_id`

---

### 6.4 enum 저장 기준

Java enum은 DB에 문자열로 저장한다.

기본 저장 방식:

```java
enumValue.name()
```

복원 방식:

```java
EnumType.valueOf(value)
```

잘못된 DB 값 보정 기준:
- 의미가 명확한 기본값이 있으면 보정
- 결과값처럼 `null` 가능성이 자연스러운 필드는 `null`로 보정
- 임의의 잘못된 값으로 강제 보정하지 않는다

예:
- `EvaluationGrade` 잘못된 값 → `AVERAGE`
- `CompensationStatus` 잘못된 값 → `IN_PROGRESS`
- `EvaluationResult` 잘못된 값 → `null`
- `ProcessingResult` 잘못된 값 → `PENDING`

---

## 7. 현재 생성된 PostgreSQL 테이블 (21개)

### 7.1 `underwriting`

```sql
CREATE TABLE underwriting (
    underwriting_id     VARCHAR(50)     NOT NULL,
    underwriter_emp_no  VARCHAR(50),
    underwriter_name    VARCHAR(100),
    underwriter_dept    VARCHAR(100),
    total_score         REAL,
    underwriting_status VARCHAR(30),
    underwriting_type   VARCHAR(30),
    underwriting_result VARCHAR(30),
    rejection_reason    TEXT,
    underwritten_at     TIMESTAMP,
    CONSTRAINT pk_underwriting PRIMARY KEY (underwriting_id)
);
```

흐름: `UnderwritingConsole → UnderwritingService → UnderwritingDBO`

---

### 7.2 `insurance_application`

```sql
CREATE TABLE insurance_application (
    application_id      VARCHAR(50)     NOT NULL,
    policy_number       VARCHAR(50),
    application_status  VARCHAR(30),
    applied_condition   VARCHAR(200),
    applied_at          TIMESTAMP,
    CONSTRAINT pk_insurance_application PRIMARY KEY (application_id)
);
```

흐름: `UnderwritingConsole → UnderwritingService → InsuranceApplicationDBO`

---

### 7.3 `underwriting_result`

```sql
CREATE TABLE underwriting_result (
    result_id               VARCHAR(50)     NOT NULL,
    underwriting_id         VARCHAR(50)     NOT NULL,
    underwriting_result     VARCHAR(30),
    rejection_reason        TEXT,
    surcharge_condition     VARCHAR(50),
    confirmed_at            TIMESTAMP,
    CONSTRAINT pk_underwriting_result PRIMARY KEY (result_id)
);
```

흐름: `UnderwritingConsole → UnderwritingService → UnderwritingResultDBO`

---

### 7.4 `underwriting_request`

```sql
CREATE TABLE underwriting_request (
    request_id              VARCHAR(50)     NOT NULL,
    request_reason          VARCHAR(50),
    request_status          VARCHAR(30),
    underwriting_type       VARCHAR(30),
    underwriting_result     VARCHAR(30),
    rejection_reason        VARCHAR(50),
    surcharge_condition     VARCHAR(50),
    applied_at              TIMESTAMP,
    applied_id              TIMESTAMP,
    CONSTRAINT pk_underwriting_request PRIMARY KEY (request_id)
);
```

흐름: `UnderwritingConsole → UnderwritingService → UnderwritingRequestDBO`

---

### 7.5 `underwriting_history`

```sql
CREATE TABLE underwriting_history (
    history_id                      VARCHAR(50)     NOT NULL,
    underwriting_id                 VARCHAR(50)     NOT NULL,
    name                            VARCHAR(100),
    age                             INTEGER,
    gender                          VARCHAR(10),
    occupation                      VARCHAR(100),
    annual_income                   NUMERIC(15, 2),
    bmi                             VARCHAR(20),
    is_smoker                       BOOLEAN         DEFAULT FALSE,
    is_medicated                    BOOLEAN         DEFAULT FALSE,
    alcohol_consumption             VARCHAR(100),
    past_medical_history            TEXT,
    surgery_history                 TEXT,
    family_history                  TEXT,
    resident_registration_number    VARCHAR(20),
    vehicle_number                  VARCHAR(50),
    vehicle_model                   VARCHAR(100),
    inquired_at                     TIMESTAMP,
    CONSTRAINT pk_underwriting_history PRIMARY KEY (history_id)
);
```

흐름: `UnderwritingConsole → UnderwritingService → UnderwritingHistoryDBO`

---

### 7.6 `accident_history`

```sql
CREATE TABLE accident_history (
    receipt_number          VARCHAR(50)     NOT NULL,
    history_id              VARCHAR(50),
    accident_type           VARCHAR(50),
    location                VARCHAR(200),
    occurred_at             TIMESTAMP,
    received_at             TIMESTAMP,
    claimed_amount          NUMERIC(15, 2),
    recognized_amount       NUMERIC(15, 2),
    diagnosis_code          VARCHAR(50),
    diagnosis_name          VARCHAR(200),
    treatment_details       TEXT,
    hospitalization_period  TIMESTAMP,
    has_surgery             BOOLEAN         DEFAULT FALSE,
    paid_at                 TIMESTAMP,
    CONSTRAINT pk_accident_history PRIMARY KEY (receipt_number)
);
```

흐름: `UnderwritingService → AccidentHistoryDBO`
주의: 콘솔 흐름에서 직접 저장되지 않고 조회 용도 위주

---

### 7.7 `endorsement`

```sql
CREATE TABLE endorsement (
    endorsement_id      VARCHAR(50)     NOT NULL,
    policy_number       VARCHAR(50)     NOT NULL,
    endorsement_type    VARCHAR(50),
    previous_content    TEXT,
    new_content         TEXT,
    change_reason       TEXT,
    underwriting_result VARCHAR(30),
    applied_at          TIMESTAMP,
    processed_at        TIMESTAMP,
    CONSTRAINT pk_endorsement PRIMARY KEY (endorsement_id)
);
```

흐름: `EndorsementConsole → EndorsementService → EndorsementDBO`

---

### 7.8 `reinstatement`

```sql
CREATE TABLE reinstatement (
    reinstatement_id    VARCHAR(50)     NOT NULL,
    policy_number       VARCHAR(50)     NOT NULL,
    underwriting_result VARCHAR(30),
    reinstatement_status VARCHAR(30),
    applied_at          TIMESTAMP,
    processed_at        TIMESTAMP,
    CONSTRAINT pk_reinstatement PRIMARY KEY (reinstatement_id)
);
```

흐름: `ReinstatementConsole → ReinstatementService → ReinstatementDBO`

---

### 7.9 `payment_collection`

```sql
CREATE TABLE payment_collection (
    collection_id               VARCHAR(50)     NOT NULL,
    policy_number               VARCHAR(50)     NOT NULL,
    due_date                    DATE,
    collected_amount            NUMERIC(15, 2),
    unpaid_amount               NUMERIC(15, 2),
    unpaid_installment_count    INTEGER,
    processing_result           VARCHAR(30),
    collected_at                TIMESTAMP,
    transfer_type               VARCHAR(30),
    collection_status           VARCHAR(30),
    CONSTRAINT pk_payment_collection PRIMARY KEY (collection_id)
);
```

흐름: `PaymentCollectionConsole → PaymentCollectionService → PaymentCollectionDBO`

---

### 7.10 `maturity_notice`

```sql
CREATE TABLE maturity_notice (
    notice_id           VARCHAR(50)     NOT NULL,
    delivery_method     VARCHAR(30),
    sent_at             TIMESTAMP,
    checked_at          TIMESTAMP,
    renewal_intention   VARCHAR(10),
    CONSTRAINT pk_maturity_notice PRIMARY KEY (notice_id)
);
```

흐름: `MaturityContractConsole → MaturityContractService → MaturityNoticeDBO`

---

### 7.11 `accident_report`

```sql
CREATE TABLE accident_report (
    report_no                   VARCHAR(50)     NOT NULL,
    policy_number               VARCHAR(50)     NOT NULL,
    accident_description        TEXT,
    damage_details              TEXT,
    accident_status             VARCHAR(50),
    document_submission_status  VARCHAR(30),
    accident_at_text            VARCHAR(30),
    created_at                  TIMESTAMP,
    CONSTRAINT pk_accident_report PRIMARY KEY (report_no)
);
```

흐름: `AccidentReportConsole → AccidentReportService → AccidentReportDBO`

---

### 7.12 `damage_investigation`

```sql
CREATE TABLE damage_investigation (
    investigation_id    VARCHAR(50)     NOT NULL,
    report_no           VARCHAR(50)     NOT NULL,
    adjuster_id         VARCHAR(50),
    medical_expense     NUMERIC(15, 2),
    lost_income         NUMERIC(15, 2),
    repair_cost         NUMERIC(15, 2),
    settlement_amount   NUMERIC(15, 2),
    fault_ratio         REAL,
    investigation_status VARCHAR(30),
    investigation_at    TIMESTAMP,
    CONSTRAINT pk_damage_investigation PRIMARY KEY (investigation_id)
);
```

흐름: `DamageInvestigationConsole → DamageInvestigationService → DamageInvestigationDBO`

---

### 7.13 `outsource_request`

```sql
CREATE TABLE outsource_request (
    request_id              VARCHAR(50)     NOT NULL,
    investigation_id        VARCHAR(50),
    partner_id              VARCHAR(50),
    request_status          VARCHAR(30),
    result                  TEXT,
    request_datetime        TIMESTAMP,
    CONSTRAINT pk_outsource_request PRIMARY KEY (request_id)
);
```

흐름: `DamageInvestigationConsole → DamageInvestigationService → OutsourceRequestDBO`

---

### 7.14 `insurance_payment`

```sql
CREATE TABLE insurance_payment (
    payment_id              VARCHAR(50)     NOT NULL,
    investigation_id        VARCHAR(50),
    payment_account         VARCHAR(100),
    processor_employee_no   VARCHAR(50),
    final_settlement_amount NUMERIC(15, 2),
    final_repair_cost       NUMERIC(15, 2),
    final_medical_expense   NUMERIC(15, 2),
    final_lost_income       NUMERIC(15, 2),
    retention_estimate      NUMERIC(15, 2),
    payment_status          VARCHAR(30),
    paid_at                 TIMESTAMP,
    CONSTRAINT pk_insurance_payment PRIMARY KEY (payment_id)
);
```

흐름: `DamageInvestigationConsole → DamageInvestigationService → InsurancePaymentDBO`

---

### 7.15 `objection`

```sql
CREATE TABLE objection (
    objection_id            VARCHAR(50)     NOT NULL,
    payment_id              VARCHAR(50),
    claimant_info           VARCHAR(200),
    objection_reason        TEXT,
    original_payment_details TEXT,
    transfer_reason         TEXT,
    adjusted_amount         NUMERIC(15, 2),
    acceptance_status       VARCHAR(30),
    CONSTRAINT pk_objection PRIMARY KEY (objection_id)
);
```

흐름: `DamageInvestigationConsole → DamageInvestigationService → ObjectionDBO`

---

### 7.16 `subrogation`

```sql
CREATE TABLE subrogation (
    subrogation_id          VARCHAR(50)     NOT NULL,
    payment_id              VARCHAR(50),
    offender_name           VARCHAR(100),
    offender_contact        VARCHAR(100),
    fault_ratio             REAL,
    payment_amount          NUMERIC(15, 2),
    payment_deadline        TIMESTAMP,
    deposit_account         VARCHAR(100),
    subrogation_status      VARCHAR(30),
    CONSTRAINT pk_subrogation PRIMARY KEY (subrogation_id)
);
```

흐름: `DamageInvestigationConsole → SubrogationService → SubrogationDBO`

---

### 7.17 `insurance`

```sql
CREATE TABLE insurance (
    product_code        VARCHAR(50)     NOT NULL,
    product_type        VARCHAR(20),
    insurance_period    VARCHAR(50),
    insured_amount      NUMERIC(20, 2),
    premium             NUMERIC(15, 2),
    maturity_refund     NUMERIC(15, 2),
    driver_age          INTEGER,
    vehicle_type        VARCHAR(100),
    building_type       VARCHAR(100),
    location            VARCHAR(200),
    vessel_type         VARCHAR(100),
    shipping_route      VARCHAR(200),
    CONSTRAINT pk_insurance PRIMARY KEY (product_code)
);
```

흐름: `InsuranceProductConsole → InsuranceProductService → InsuranceDBO`

---

### 7.18 `authorization_request`

```sql
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
```

흐름: `InsuranceProductConsole → InsuranceProductService → AuthorizationDBO`
주의: 테이블명은 `authorization_request` 고정. PostgreSQL 예약어 충돌 방지.

---

### 7.19 `payout`

```sql
CREATE TABLE payout (
    payout_id               VARCHAR(50)     NOT NULL,
    policy_number           VARCHAR(50)     NOT NULL,
    processor               VARCHAR(100),
    payment_type            VARCHAR(30),
    calculation_basis       VARCHAR(50),
    calculated_amount       NUMERIC(15, 2),
    deduction_item          VARCHAR(200),
    final_payment_amount    NUMERIC(15, 2),
    approved_at             TIMESTAMP,
    paid_at                 TIMESTAMP,
    payout_status           VARCHAR(30),
    CONSTRAINT pk_payout PRIMARY KEY (payout_id)
);
```

흐름: `PayoutConsole → PayoutService → PayoutDBO`

---

### 7.20 `compensation_evaluation`

```sql
CREATE TABLE compensation_evaluation (
    evaluation_id           VARCHAR(50)     NOT NULL,
    evaluation_month        INTEGER,
    evaluation_status       VARCHAR(30),
    evaluation_result       VARCHAR(30),
    submission_agency_name  VARCHAR(200),
    damage_amount           NUMERIC(15, 2),
    damage_analysis_result  TEXT,
    compensation_statistics TEXT,
    CONSTRAINT pk_compensation_evaluation PRIMARY KEY (evaluation_id)
);
```

흐름: `CompensationEvaluationConsole → CompensationEvaluationService → CompensationEvaluationDBO`

---

### 7.21 `partner`

```sql
CREATE TABLE partner (
    id                  VARCHAR(50)     NOT NULL,
    partner_name        VARCHAR(200)    NOT NULL,
    partner_type        VARCHAR(100),
    contact             VARCHAR(100),
    responsibility      VARCHAR(200),
    evaluation_grade    VARCHAR(20),
    CONSTRAINT pk_partner PRIMARY KEY (id)
);
```

흐름: `PartnerConsole → PartnerService → PartnerDBO`

---

## 8. 현재 DB 전환 미완료 기능

### 8.1 공동인수

관련 파일:
- `UnderwritingConsole.java`
- `Coinsurance.java`, `Coinsurer.java`
- `CoinsuranceDBO.java`, `CoinsurerDBO.java` ← 현재 스텁

현재 구조:
- Console private 흐름으로만 처리
- 객체 저장 없음

필요성: 중간
난이도: 높음
위험도: 높음

예상 테이블:
- `coinsurance`
- `coinsurer`

주의:
- 언더라이팅 흐름 이후 분리 설계 필요
- Contract FK 연결은 보류

---

### 8.2 재보험

관련 파일:
- `UnderwritingConsole.java`
- `Reinsurance.java`
- `ReinsuranceDBO.java` ← 현재 스텁

현재 구조:
- Console private 흐름으로만 처리
- DB 저장 없음

필요성: 중간
난이도: 높음
위험도: 높음

예상 테이블:
- `reinsurance`

주의:
- 언더라이팅 흐름 이후 분리 설계 필요

---

### 8.3 계약 기반 (Contract)

관련 파일:
- `Contract.java`
- `ContractDBO.java` ← 현재 스텁
- ContractService 없음

현재 구조:
- 다른 기능들이 `policy_number` 문자열만 참조
- 실제 Contract 테이블 없음

필요성: 높음
난이도: 높음
위험도: 높음

예상 테이블:
- `contract`

주의:
- 여러 기능의 FK 기반 테이블이 될 수 있음
- 지금 바로 기존 테이블에 FK를 일괄 적용하지 않는다
- ContractService 설계가 선행되어야 함

---

### 8.4 기타 스텁 DBO (낮은 우선순위)

현재 스텁 상태이나 메인 유스케이스에서 호출되지 않는 DBO:
- `AccountDBO` — Account 테이블 없음
- `InsuredPersonDBO` — InsuredPerson 테이블 없음
- `ManagerDBO` — Manager 테이블 없음
- `TransferDBO` — Transfer 테이블 없음
- `DocumentDBO` — Document 테이블 없음

주의: 위 5개 DBO는 어떤 Service에서도 import되지 않아 실행 중 호출되지 않음.

---

## 9. 남은 작업 우선순위

현재 메인 유스케이스 흐름은 모두 DB 전환 완료된 상태이다.

남은 항목은 다음 순서로 진행한다.

1. Contract 기반 설계 (다른 기능의 FK 기반)
2. 공동인수 (coinsurance, coinsurer)
3. 재보험 (reinsurance)
4. Account, InsuredPerson, Manager, Transfer, Document (필요 시)

---

## 10. 팀원 분담안 (현재 기준)

### 10.1 완료된 분담 내역

| 기능 | 담당 | 상태 |
|---|---|---|
| 협력업체 관리 | - | ✅ 완료 |
| 보상평가 관리 | - | ✅ 완료 |
| 상품 개발 | - | ✅ 완료 |
| 제지급금 관리 | - | ✅ 완료 |
| 사고 접수 | - | ✅ 완료 |
| 분납/수금 관리 | - | ✅ 완료 |
| 배서 관리 | - | ✅ 완료 |
| 부활 관리 | - | ✅ 완료 |
| 만기계약 관리 | - | ✅ 완료 |
| 보험청약 심사 전체 | ChanYoung | ✅ 완료 |
| 손해조사/지급/이의/구상/위탁 | ChanYoung | ✅ 완료 |

### 10.2 남은 분담

| 기능 | 예상 테이블 | 난이도 |
|---|---|---|
| Contract 기반 설계 | `contract` | 높음 |
| 공동인수 | `coinsurance`, `coinsurer` | 높음 |
| 재보험 | `reinsurance` | 높음 |

---

## 11. 팀원 작업 공통 절차

각 팀원은 다음 순서로 작업한다.

1. 대상 기능 코드 구조 분석
2. Model 필드 확인
3. Service 임시 저장 구조 확인
4. DBO 현재 메서드 확인
5. PostgreSQL 테이블 DDL 설계
6. 사용자/팀 확인 후 테이블 생성
7. DBO CRUD 구현
8. Service에서 메모리 저장소 또는 저장 시뮬레이션 제거/대체
9. Console은 최소 수정
10. 컴파일
11. 기능 테스트
12. DB 영속성 테스트
13. `db.properties`, `bin/`, `*.jar` 제외 확인
14. 커밋/푸시
15. 변경 파일과 테스트 결과 보고

---

## 12. 단계별 작업 승인 문구 기준

코드 수정 전에는 반드시 다음처럼 범위를 명확히 한다.

```text
○ 단계: [기능명] DB 전환 코드 수정을 진행해라.

브랜치:
Gihyeon_code_3rd_ChanYoung

수정 대상:
1. src/db/...
2. src/service/...
3. 필요 시 src/console/...

수정 목표:
- Service의 메모리 저장 구조 또는 저장 시뮬레이션을 DBO 호출로 전환한다.
- DBO는 PostgreSQL [테이블명] 테이블 CRUD를 담당한다.
- Console → Service → DBO → DBA → PostgreSQL DB 구조를 유지한다.

허용:
- DBO save/update/delete 반환형 boolean 변경
- PreparedStatement/ResultSet 기반 CRUD 구현
- ResultSet → Model 변환 helper 추가
- Service 보조 메서드 추가
- Console null 방어 또는 저장 실패 메시지 최소 추가

금지:
- 새 package/class/enum 추가 금지
- Model 필드 추가 금지
- DBA.java 수정 금지
- Main.java 수정 금지
- MyBatis 적용 금지
- 기존 메뉴 기능 삭제 금지
- Console에서 DBO/DBA 직접 호출 금지
- Model에서 DBO/DBA 직접 호출 금지

수정 후:
- 전체 Java 컴파일 확인
- PostgreSQL 연결/조회 확인
- 메뉴 기능 테스트
- 프로그램 재실행 후 DB 영속성 확인
- db.properties, bin/, *.jar 제외 확인
- 이상 없으면 직접 커밋/푸시

커밋 메시지:
feat: [기능명] PostgreSQL 연동
```

---

## 13. 계속 보류하는 항목

다음 항목은 아직 진행하지 않는다.

- F 입력 검증 전체 정리
- G 손해조사 위탁 ↔ 협력업체 완전 연결
- MyBatis 적용
- 새 Repository/DAO 패키지 생성
- Contract FK를 기존 테이블에 일괄 적용
- PaymentCollection.calculateLateFee() 구현
- 실제 자동이체 외부 시스템 연동
- 실제 은행 API 연동
- 공동인수/재보험을 언더라이팅과 동시에 한 번에 전환
- 모델 필드 대량 추가
- 기존 메뉴 흐름 삭제
- 기존 유스케이스 흐름 무단 변경

---

## 14. MyBatis 적용 기준

MyBatis는 지금 적용하지 않는다.

현재는 다음 구조를 유지한다.

```text
Service → DBO → DBA → PostgreSQL
```

나중에 MyBatis를 적용한다면 다음처럼 본다.

```text
Service → DBO → MyBatis Mapper → DB
```

중요:
- Service가 Mapper를 직접 호출하지 않는다.
- DBO 내부 구현을 MyBatis 방식으로 바꾸는 방향으로 본다.
- Console/Service 구조를 유지한다.

---

## 15. 최종 요약

현재 프로젝트는 기존 메모리 저장소/시뮬레이션 기반 기능을 PostgreSQL DB 기반으로 전환 완료하였다.

**21개 테이블, 21개 DBO 모두 실제 JDBC(PreparedStatement) 기반으로 구현 완료.**

```text
underwriting / insurance_application / underwriting_result / underwriting_request
underwriting_history / accident_history / endorsement / reinstatement
payment_collection / maturity_notice / accident_report / damage_investigation
outsource_request / insurance_payment / objection / subrogation
insurance / authorization_request / payout / compensation_evaluation / partner
```

남은 기능은 공동인수, 재보험, Contract 기반 설계이다.

앞으로도 모든 DB 전환은 다음 구조를 유지한다.

```text
Console → Service → DBO → DBA → PostgreSQL DB
```

가장 중요한 원칙은 다음과 같다.

1. Console은 Service만 호출한다.
2. Model은 DB를 모른다.
3. DBO는 DAO 역할을 한다.
4. DBA는 DB 연결 공통 기반이다.
5. 새 패키지를 만들지 않는다.
6. MyBatis는 아직 적용하지 않는다.
7. 기존 메뉴와 유스케이스 흐름을 삭제하지 않는다.
8. Model 필드는 승인 없이 추가하지 않는다.
9. DB 테이블은 직접 생성한다.
10. 코드 수정 후에는 컴파일, 기능 테스트, DB 영속성 테스트, Git 제외 파일 확인을 반드시 수행한다.
