# BS_INS_PRODUCT — 상품 관리 모듈 작업 문서

> 신동아화재 보험사 시스템 웹 전환 프로젝트의 **상품 관리(상품 설계 + 상품 인가 요청)** 모듈을 `origin/WEB` 브랜치 기준 `claim` 모듈과 동일한 디자인·패턴·구조로 신규 구현한 작업의 전체 기록.
>
> 다른 팀원이 이 문서만 읽어도 모듈 전체를 이해할 수 있고, 다른 AI 어시스턴트가 이어받아 작업해도 일관성을 유지할 수 있도록 작성됨.

---

## 0. 한 줄 요약

| 항목 | 내용 |
|---|---|
| 모듈명 | `product` |
| 구현 유스케이스 | `상품을 설계한다`, `상품 인가를 요청한다` (2건) |
| 기준 자료 | RFP → 유스케이스 다이어그램 → 유스케이스 내부 시나리오 → 클래스 다이어그램 (CLAUDE.md 우선순위 그대로) |
| 참고 모듈 | `claim` (사고접수/손해조사/지급/구상/이의제기) |
| 백엔드 | Spring Boot 3.3.6 + MyBatis + PostgreSQL |
| 프론트 | React 18 + Vite + TypeScript + React Router |
| 외부 시스템(금융감독원) | 호출 없음. 내부 상태 전이로 대체 (CLAUDE.md "외부 API 미구현" 원칙) |

---

## 1. 아키텍처와 따른 규칙

### 1.1 레이어 구조 (CLAUDE.md 그대로)

```
React Page → API Module → RestController → Service → Mapper(인터페이스) → Mapper XML → PostgreSQL
```

- **의존성 방향**: 위에서 아래로만 흐름. 도메인 객체는 Service/Mapper/Console을 모름.
- **저장 흐름**: Service가 도메인 객체 생성 → 채워서 Mapper에 넘김.
- **조회 흐름**: Mapper XML의 `resultMap`이 DB 결과를 도메인 객체에 담아 Service에 반환.

### 1.2 절대 위반 금지 항목 (LLM_WIKI / CLAUDE.md 발췌)

- **claim 모듈 코드는 어떤 이유로도 수정 금지** — 참고만 함. 실제 일관성은 동일 클래스명/CSS 클래스를 별도 파일에서 재구성해 확보.
- 기존 Service/DAO/Model **임의 삭제 금지**, 클래스명/메서드명/패키지명 **임의 변경 금지**.
- Controller에 비즈니스 로직 작성 금지.
- Mapper 인터페이스에 SQL 어노테이션 작성 금지 — SQL은 XML에만.
- 도메인이 Service/Mapper/Console 호출 금지 (의존성 방향 위반).
- PostgreSQL 외 DB 문법 사용 금지.
- **외부 API(금융감독원/재보험사/공동인수사/ICIS) 메서드 새로 구현 금지** — 범위 밖.

### 1.3 본 모듈에서 적용한 claim 패턴 매핑

| claim | product |
|---|---|
| `claim/controller/AccidentReportController` | `product/controller/ProductController` |
| `claim/service/AccidentReportApplicationService` | `product/service/ProductDesignApplicationService` |
| `claim/mapper/AccidentReportMapper` (+ XML) | `product/mapper/ProductMapper` (+ XML) |
| `claim/service/SubrogationApplicationService` (조건부 전이 패턴) | `product/service/AuthorizationApplicationService` |
| `claim/dto/AccidentReportCreateRequest`, `AccidentReportResponse` | `product/dto/ProductDesignRequest`, `ProductResponse` |
| `claim/dto/SubrogationEligibilityResponse` | `product/dto/AuthorizationEligibilityResponse` |
| `frontend/src/types/claim.ts` | `frontend/src/types/product.ts` |
| `frontend/src/api/claimApi.ts` | `frontend/src/api/productApi.ts` |
| `frontend/src/pages/claim/AccidentReportPage` (생성+조회 페이지) | `frontend/src/pages/product/ProductDesignPage` |
| `frontend/src/pages/claim/SubrogationPage` (조회→조건부 액션 페이지) | `frontend/src/pages/product/ProductAuthorizationPage` |
| `components/claim/AccidentReportForm` (4파일 피커 attachment 패턴) | `components/product/AuthorizationForm` (4파일 피커) |

---

## 2. 파일 인벤토리

### 2.1 신규 생성 파일

**백엔드**
```
src/enums/
 ├─ ProductStatus.java
 └─ AuthorizationStatus.java

src/product/
 ├─ controller/
 │   ├─ package-info.java
 │   ├─ ProductController.java
 │   └─ AuthorizationController.java
 ├─ dto/
 │   ├─ package-info.java
 │   ├─ ProductDesignRequest.java
 │   ├─ ProductResponse.java
 │   ├─ AuthorizationCreateRequest.java
 │   ├─ AuthorizationRevisionRequest.java
 │   ├─ AuthorizationResponse.java
 │   └─ AuthorizationEligibilityResponse.java
 ├─ mapper/
 │   ├─ package-info.java
 │   ├─ ProductMapper.java
 │   └─ AuthorizationMapper.java
 └─ service/
     ├─ ProductDesignApplicationService.java
     └─ AuthorizationApplicationService.java

src/main/resources/mappers/product/
 ├─ ProductMapper.xml
 └─ AuthorizationMapper.xml
```

**프론트엔드**
```
frontend/src/
 ├─ types/product.ts
 ├─ api/productApi.ts
 ├─ pages/product/
 │   ├─ ProductDesignPage.tsx
 │   └─ ProductAuthorizationPage.tsx
 └─ components/product/
     ├─ AlertMessage.tsx
     ├─ ProductStatusBadge.tsx
     ├─ AuthorizationStatusBadge.tsx
     ├─ ProductWorkflowSteps.tsx
     ├─ ProductDesignForm.tsx
     ├─ ProductDesignResultCard.tsx
     ├─ ProductDetailCard.tsx
     ├─ ProductSearchBox.tsx
     ├─ AuthorizationEligibilityCard.tsx
     ├─ AuthorizationForm.tsx
     ├─ AuthorizationStatusCard.tsx
     └─ AuthorizationApprovalPanel.tsx
```

### 2.2 기존 파일 수정

| 파일 | 변경 내용 |
|---|---|
| `src/com/boonsan/BoonsanApplication.java` | `scanBasePackages`에 `"product"` 추가 |
| `src/config/MyBatisConfig.java` | `@MapperScan` 배열에 `"product.mapper"` 추가 |
| `src/main/resources/schema.sql` | `product`, `product_authorization` 테이블 추가 (멱등 `CREATE TABLE IF NOT EXISTS` + `ALTER TABLE ADD COLUMN IF NOT EXISTS`) |
| `src/model/insurance/Insurance.java` | 시나리오 필드 13개 추가, 생성자, getter/setter. **기존 abstract 메서드/skeleton 빈 메서드 전부 유지**. skeleton의 `void getInsuranceType()`와 JavaBean getter 충돌을 피하기 위해 새 필드명은 `insuranceTypeCode`로. |
| `src/model/insurance/AutoInsurance.java` | 풀필드 생성자 추가, getter/setter. 기존 `driverAge`, `vehicleType` + skeleton 메서드 유지 |
| `src/model/insurance/FireInsurance.java` | 동일 패턴 (`buildingType`, `location`) |
| `src/model/insurance/MarineInsurance.java` | 동일 패턴 (`shippingRoute`, `vesselType`) |
| `src/model/insurance/Authorization.java` | 시나리오 필드 추가 (`productCode`, `authorizationStatus`, 첨부 4종 `*FileName`, `revisionRequest`, `updatedAt`), 생성자, getter/setter. skeleton 빈 메서드 유지 |
| `frontend/src/routes/AppRouter.tsx` | `/products/design`, `/products/authorization` 라우트 2개 추가 |
| `frontend/src/components/layout/Sidebar.tsx` | `getMenuHref()`에 `product-design`/`product-approval` 분기 추가. 사이드바 메뉴 항목 자체는 origin/WEB에 이미 존재했음 (라우팅만 빠져 있었음) |

### 2.3 손대지 않은 영역

- `src/claim/**` (모든 파일)
- `frontend/src/{api,components,pages,types}/claim*` 와 `claim/` 디렉토리 전체
- 기존 console 진입점 `src/Main.java` 및 `src/service/**` (옛 콘솔 코드 — 웹 전환 후 미사용이지만 보존)
- `model/insurance/FinancialSupervisoryService` — 외부 시스템 도메인. CLAUDE.md "외부 API 미구현" 원칙 따라 skeleton 그대로 유지

---

## 3. enum 상태값

### 3.1 `enums.ProductStatus` — 상품 자체 상태

| 값 | 의미 | 시나리오 근거 |
|---|---|---|
| `DESIGN_COMPLETED` | 설계 완료 | "상품을 설계한다" Basic Path 16: 시스템은 상품 상태를 설계완료로 변경 |
| `AUTHORIZATION_REQUESTED` | 인가 요청 중 (대기) | "상품 인가를 요청한다" Basic Path 6: 제출 상태를 '인가요청'으로 변경 후 결과 대기 |
| `AUTHORIZED` | 인가 완료 | Basic Path 9: 인가 결과에 따라 상품 상태를 '인가 완료'로 변경 |
| `AUTHORIZATION_REJECTED` | 인가 불허 | Basic Path 9: 상품 상태를 '인가 불허'로 변경 |
| `REVISION_REQUESTED` | 보완 요청 | Basic Path 9 / Alternate A2: 보완 요청 사항 출력 |
| `TEMP_SAVED` | 임시 저장 | "상품을 설계한다" E1, "인가 요청" E1 — 현재 UI 미구현, enum만 예약 |

### 3.2 `enums.AuthorizationStatus` — 인가 요청 1건의 상태

| 값 | 의미 | 시나리오 근거 |
|---|---|---|
| `REQUESTED` | 인가 요청됨 (제출) | "인가 요청" Basic Path 6 |
| `APPROVED` | 승인 | Basic Path 9 인가 완료 |
| `REJECTED` | 불허 | Basic Path 9 인가 불허 |
| `REVISION_REQUIRED` | 보완 요청 | Alternate A2 |
| `CANCELLED` | 취소 | Alternate A1: 상품개발자가 인가 요청을 취소 |

### 3.3 상태 전이 규칙

```
[상품 설계]
사용자 폼 저장 → ProductStatus = DESIGN_COMPLETED

[인가 요청 등록]
ProductStatus { DESIGN_COMPLETED | REVISION_REQUESTED } 일 때만 요청 가능
요청 등록 시:
  product.product_status        → AUTHORIZATION_REQUESTED
  product_authorization.status  → REQUESTED

[인가 결과 처리 — REQUESTED 상태에서만 가능]
승인:   AuthorizationStatus → APPROVED          + ProductStatus → AUTHORIZED
불허:   AuthorizationStatus → REJECTED          + ProductStatus → AUTHORIZATION_REJECTED
보완:   AuthorizationStatus → REVISION_REQUIRED + ProductStatus → REVISION_REQUESTED
취소:   AuthorizationStatus → CANCELLED         + ProductStatus → DESIGN_COMPLETED (재요청 가능)
```

규칙은 `AuthorizationApplicationService.transitionState()`에 캡슐화. REQUESTED가 아닌 상태에서 전이 시도하면 `IllegalArgumentException("Authorization is not in REQUESTED state.")` 반환 → GlobalExceptionHandler가 400 응답.

---

## 4. DB 스키마

> `application.properties`에 `spring.sql.init.mode=always` 이므로 앱 시작 시 `src/main/resources/schema.sql` 자동 적용. 모든 DDL이 멱등(`IF NOT EXISTS`)이라 여러 번 실행해도 안전.

### 4.1 `product` 테이블

단일 테이블 전략. `insurance_type` 컬럼을 discriminator로 사용해 AUTO/FIRE/MARINE 구분. 서브타입 전용 컬럼은 nullable.

```sql
CREATE TABLE IF NOT EXISTS product (
    product_code              VARCHAR(50)  PRIMARY KEY,
    product_name              VARCHAR(255) NOT NULL,
    insurance_type            VARCHAR(50)  NOT NULL,  -- AUTO | FIRE | MARINE
    target_customer           VARCHAR(255),
    sales_channel             VARCHAR(255),
    insurance_period          VARCHAR(100),
    payment_period            VARCHAR(100),
    insured_amount            NUMERIC(15,2),
    premium                   NUMERIC(15,2),
    maturity_refund           NUMERIC(15,2),
    main_coverage             TEXT,    -- 주담보+보장내용 (자유 텍스트)
    subscription_conditions   TEXT,    -- 가입조건 (자유 텍스트)
    rate_information          TEXT,    -- 요율 정보 (자유 텍스트)
    special_contract_info     TEXT,    -- 특약 정보 (자유 텍스트)
    product_status            VARCHAR(50)  NOT NULL,  -- ProductStatus enum
    driver_age                INTEGER,                -- AUTO 전용
    vehicle_type              VARCHAR(100),           -- AUTO 전용
    building_type             VARCHAR(100),           -- FIRE 전용
    location                  VARCHAR(255),           -- FIRE 전용
    shipping_route            VARCHAR(255),           -- MARINE 전용
    vessel_type               VARCHAR(100),           -- MARINE 전용
    created_at                TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_product_insurance_type ON product (insurance_type);
```

### 4.2 `product_authorization` 테이블

상품 1개당 여러 인가 요청 이력을 가질 수 있게 1:N 구조(`requestId`가 PK, `productCode` 외래 개념). 첨부 4종은 각각 컬럼으로 분리(claim의 `accident_report`와 동일 결).

```sql
CREATE TABLE IF NOT EXISTS product_authorization (
    request_id                       VARCHAR(50)  PRIMARY KEY,
    product_code                     VARCHAR(50)  NOT NULL,
    requested_at                     TIMESTAMP    NOT NULL,
    approved_at                      TIMESTAMP,                       -- APPROVED 시점만
    is_approved                      BOOLEAN      NOT NULL DEFAULT FALSE,
    request_reason                   TEXT,
    submission_agency_name           VARCHAR(255),
    authorization_status             VARCHAR(50)  NOT NULL,  -- AuthorizationStatus enum
    product_description_file_name    VARCHAR(255),
    terms_and_conditions_file_name   VARCHAR(255),
    rate_schedule_file_name          VARCHAR(255),
    product_evidence_file_name       VARCHAR(255),
    revision_request                 TEXT,
    updated_at                       TIMESTAMP    NOT NULL
);
```

---

## 5. 도메인 모델

### 5.1 `model.insurance.Insurance` (abstract)

| 필드 (protected) | 타입 | 비고 |
|---|---|---|
| `productCode` | String | PK |
| `productName` | String | |
| `insuranceTypeCode` | String | discriminator. AUTO/FIRE/MARINE. 필드명에 `Code` 붙인 이유는 skeleton 메서드 `void getInsuranceType()`과 JavaBean 컨벤션 충돌 회피 |
| `targetCustomer` | String | |
| `salesChannel` | String | |
| `insurancePeriod` | String | |
| `paymentPeriod` | String | |
| `insuredAmount` | BigDecimal | |
| `premium` | BigDecimal | 사용자 직접 입력 (자동 산정 미구현) |
| `maturityRefund` | BigDecimal | 사용자 직접 입력 |
| `mainCoverage` | String | TEXT |
| `subscriptionConditions` | String | TEXT |
| `rateInformation` | String | TEXT |
| `specialContractInfo` | String | TEXT |
| `productStatus` | ProductStatus | |
| `createdAt` | LocalDateTime | |

**유지된 skeleton 메서드** (구현 없음, CLAUDE.md "기존 메서드명 임의 변경 금지"):
- `abstract void calculatePremium()`, `abstract void calculateMaturityRefund()`
- `void changeProductStatus()`, `void getInsuranceType()`, `void saveProductInfo()`

### 5.2 서브클래스 — `AutoInsurance`, `FireInsurance`, `MarineInsurance`

각각 전용 필드 + 부모 super 호출 생성자 + 자체 getter/setter. skeleton 메서드 유지:
- AutoInsurance: `driverAge:int`, `vehicleType:String`, `void getAccidentHistory()`
- FireInsurance: `buildingType:String`, `location:String`, `void analyzeRiskFactors()`, `void setCoverageScope()`
- MarineInsurance: `shippingRoute:String`, `vesselType:String`, `void evaluateRiskLevel()`, `void manageShippingInfo()`

### 5.3 `model.insurance.Authorization`

| 필드 | 타입 | 비고 |
|---|---|---|
| `requestId` | String | PK |
| `productCode` | String | |
| `requestedAt` | LocalDateTime | |
| `approvedAt` | LocalDateTime | APPROVED 시점에만 채워짐 |
| `isApproved` | boolean | |
| `requestReason` | String | TEXT |
| `submissionAgencyName` | String | |
| `authorizationStatus` | AuthorizationStatus | |
| `productDescriptionFileName` | String | 상품설명서 |
| `termsAndConditionsFileName` | String | 약관 |
| `rateScheduleFileName` | String | 요율서 |
| `productEvidenceFileName` | String | 상품개발 근거자료 |
| `revisionRequest` | String | TEXT, REVISION_REQUIRED 전이 시만 채워짐 |
| `updatedAt` | LocalDateTime | |

유지된 skeleton 메서드: `applyAuthorizationResult()`, `cancelAuthorizationRequest()`, `sendAuthorizationRequest()`, `updateProductStatus()`.

### 5.4 MyBatis 다형성 매핑

`ProductMapper.xml`에 discriminator 패턴:

```xml
<resultMap id="ProductBaseResultMap" type="model.insurance.Insurance">
    <!-- 공통 컬럼 매핑 -->
    <discriminator javaType="string" column="insurance_type">
        <case value="AUTO"   resultMap="AutoInsuranceResultMap"/>
        <case value="FIRE"   resultMap="FireInsuranceResultMap"/>
        <case value="MARINE" resultMap="MarineInsuranceResultMap"/>
    </discriminator>
</resultMap>

<resultMap id="AutoInsuranceResultMap" type="model.insurance.AutoInsurance" extends="ProductBaseResultMap">
    <result property="driverAge"   column="driver_age"/>
    <result property="vehicleType" column="vehicle_type"/>
</resultMap>
<!-- Fire, Marine 동일 패턴 -->
```

INSERT에서는 `_parameter instanceof model.insurance.AutoInsurance` 등으로 분기해 서브타입 컬럼을 채우거나 NULL.

---

## 6. REST API 명세

CORS는 `WebConfig`에 `localhost:5173`, `127.0.0.1:5173`, `boonsan-frontend.vercel.app`, `mjusw.site` 허용.

응답 봉투 (`common.ApiResponse`):
```json
{ "success": true, "data": { ... }, "message": "...", "errorCode": null }
```

### 6.1 상품 설계 — `/api/products`

| 메서드 | 경로 | 설명 | 요청 본문 | 응답 |
|---|---|---|---|---|
| POST | `/api/products` | 상품 설계 등록 | `ProductDesignRequest` | `ProductResponse` |
| GET | `/api/products` | 전체 상품 목록 (최신순) | - | `ProductResponse[]` |
| GET | `/api/products/{productCode}` | 단건 조회 | - | `ProductResponse` |

`ProductDesignRequest` 필드:
```ts
{
  productName: string;            // 필수
  insuranceTypeCode: 'AUTO'|'FIRE'|'MARINE';  // 필수
  targetCustomer?: string;
  salesChannel?: string;
  insurancePeriod?: string;
  paymentPeriod?: string;
  insuredAmount: number;          // 필수, >0
  premium?: number;
  maturityRefund?: number;
  mainCoverage?: string;
  subscriptionConditions?: string;
  rateInformation?: string;
  specialContractInfo?: string;
  // 서브타입 전용 (insuranceTypeCode에 맞는 것만 채움)
  driverAge?: number; vehicleType?: string;
  buildingType?: string; location?: string;
  shippingRoute?: string; vesselType?: string;
}
```

`productCode`는 서비스가 자동 생성: `PRD-{TYPE}-{YEAR}-{6자리랜덤}` 형식 (e.g. `PRD-AUTO-2026-783910`).

### 6.2 상품 인가 — `/api/products/{productCode}/authorization`

| 메서드 | 경로 | 설명 | 요청 본문 | 응답 |
|---|---|---|---|---|
| GET | `.../authorization/eligibility` | 인가 요청 가능 여부 | - | `AuthorizationEligibilityResponse` |
| GET | `.../authorization` | 최신 인가 1건 조회 (없으면 404) | - | `AuthorizationResponse` |
| POST | `.../authorization` | 인가 요청 등록 | `AuthorizationCreateRequest` | `AuthorizationResponse` |
| PATCH | `.../authorization/approve` | 승인 | - | `AuthorizationResponse` |
| PATCH | `.../authorization/reject` | 불허 | - | `AuthorizationResponse` |
| PATCH | `.../authorization/revision` | 보완 요청 | `AuthorizationRevisionRequest` | `AuthorizationResponse` |
| PATCH | `.../authorization/cancel` | 요청 취소 (상품개발자) | - | `AuthorizationResponse` |

`AuthorizationCreateRequest`:
```ts
{
  requestReason: string;          // 필수
  submissionAgencyName: string;   // 필수
  productDescriptionFileName?: string;   // 첨부 파일명만 저장
  termsAndConditionsFileName?: string;
  rateScheduleFileName?: string;
  productEvidenceFileName?: string;
}
```

`AuthorizationRevisionRequest`:
```ts
{ revisionRequest: string; }  // 필수
```

`requestId`: `AUTH-{YEAR}-{6자리랜덤}` (e.g. `AUTH-2026-114520`).

### 6.3 예외 → HTTP

`exception.GlobalExceptionHandler`에 등록됨:
- `MethodArgumentNotValidException` → `400 VALIDATION_ERROR`
- `IllegalArgumentException` → `400 INVALID_REQUEST`
- `NoSuchElementException` → `404 NOT_FOUND`

---

## 7. 프론트엔드 구조

### 7.1 타입 (`frontend/src/types/product.ts`)

- `InsuranceTypeCode = 'AUTO' | 'FIRE' | 'MARINE'`
- `ProductStatus` (백엔드 enum과 동기)
- `AuthorizationStatus` (백엔드 enum과 동기)
- 모든 Request/Response 인터페이스
- 라벨 맵 3종 (`INSURANCE_TYPE_LABELS`, `PRODUCT_STATUS_LABELS`, `AUTHORIZATION_STATUS_LABELS`)과 헬퍼 함수

### 7.2 API 모듈 (`frontend/src/api/productApi.ts`)

전 엔드포인트 1:1 함수 export. claim의 `apiClient.apiRequest<T>()` 그대로 사용 — 응답 envelope 처리, `ApiError` 던지기 모두 공통.

### 7.3 페이지 레이아웃 패턴

**`ProductDesignPage`** (= `claim/AccidentReportPage` 구조)
- 페이지 헤더 (breadcrumb + 제목)
- `<ProductWorkflowSteps currentStepId="design" />`
- `<section className="content-grid">` 2-column 레이아웃: 좌측 폼, 우측 결과·상세 stack
- 하단 `<ProductSearchBox />` (상품 코드로 조회)

**`ProductAuthorizationPage`** (= `claim/SubrogationPage` 구조)
- 페이지 헤더
- WorkflowSteps (`currentStepId="authorization"`)
- 단일 컬럼 stack: 검색 패널 → eligibility 카드 → 폼 (가능시) / 또는 상태 카드 + 결과 처리 패널
- 2-column 안 씀. 이유: 가로 분할이 부자연스러워서 Subrogation 패턴(세로 stack)이 더 자연스러움.

### 7.4 컴포넌트 매핑

| 파일 | 역할 | claim 대응 |
|---|---|---|
| `AlertMessage` | 성공/실패 토스트 | `claim/AlertMessage` 동일 동작 |
| `ProductStatusBadge` | 상품 상태 뱃지 | `claim/StatusBadge` |
| `AuthorizationStatusBadge` | 인가 상태 뱃지 | 동상 |
| `ProductWorkflowSteps` | 상단 흐름 인디케이터 | `claim/WorkflowSteps`. **steps: 설계 → 인가 요청 2개만**. 인가 완료/판매 개시는 유스케이스 범위 밖이라 미포함 |
| `ProductDesignForm` | 상품 설계 입력 | `AccidentReportForm` 패턴. 5섹션(기본정보/담보·보장/요율/특약/서브타입별). 서브타입 섹션은 `insuranceTypeCode` 선택 시에만 노출 |
| `ProductDesignResultCard` | 저장 직후 코드 발급 카드 | `AccidentReportResultCard` |
| `ProductDetailCard` | 상품 상세 | `AccidentReportDetailCard` |
| `ProductSearchBox` | 상품 코드 검색 | `AccidentReportSearchBox` |
| `AuthorizationEligibilityCard` | 인가 가능 여부 | `SubrogationEligibilityCard` 패턴 (`approval-card next-step-panel`) |
| `AuthorizationForm` | 인가 요청 폼 | claim의 4파일 피커 패턴 (`AccidentReportForm`의 `attachmentFields`/`handleFileChange`/`handleClearFile` 패턴) 차용 |
| `AuthorizationStatusCard` | 인가 요청 상세 | `SubrogationDetailCard` 패턴 |
| `AuthorizationApprovalPanel` | 승인/불허/보완/취소 액션 | `SubrogationCompletePanel` 패턴 |

### 7.5 라우팅

`AppRouter.tsx`:
```tsx
<Route path="/products/design"          element={<ProductDesignPage />} />
<Route path="/products/authorization"   element={<ProductAuthorizationPage />} />
```

`Sidebar.tsx`의 `getMenuHref()`에 분기 추가:
```ts
if (itemId === 'product-design')   return '/products/design';
if (itemId === 'product-approval') return '/products/authorization';
```

(메뉴 항목 자체는 origin/WEB의 Sidebar에 이미 존재했음 — 라우팅만 빠져 있어서 `'#'`로 빠지던 것을 연결)

---

## 8. 알려진 단순화 / TODO

> 사용자 명시: "텍스트 영역으로 날먹한 내용 나중에 구체적으로 할 수도 있음"

### 8.1 텍스트로 통합한 항목 (향후 구조화 후보)

| 현재 컬럼 | 시나리오 원본 (Basic Path) | 향후 분리 가능 |
|---|---|---|
| `main_coverage` (TEXT) | 주담보명, 보장내용, 보험가입금액 한도, 면책조건, 지급조건 | 5개 컬럼 또는 별도 `product_coverage` 테이블 |
| `subscription_conditions` (TEXT) | 가입가능연령, 가입제한조건, 직업조건, 건강조건, 계약제한사유 | 5개 컬럼 또는 별도 `product_subscription_condition` |
| `rate_information` (TEXT) | 기초요율, 위험률, 예정이율, 사업비율, 할인/할증요율 | 5개 NUMERIC 컬럼 + 적용요율/예상보험료/손익예상치 산출 로직 |
| `special_contract_info` (TEXT) | 특약명, 보장내용, 가입조건, 특약보험료, 중복가입 가능여부 | 1:N `product_special_contract` 테이블 (특약 여러 개 가능성) |

분리할 때 권장 순서: **요율 → 특약 → 담보 → 가입조건**. 요율은 계산식이 있으니 가장 가치 있고, 특약은 자연스레 1:N이라 도메인 분리가 명확함.

### 8.2 시나리오 미구현 항목

- **보험요율 자동 산정** (Basic Path 9-10): 현재는 사용자가 `premium`을 직접 숫자 입력. `Insurance.calculatePremium()` skeleton 메서드를 실제 구현해서 Service에서 호출하도록 변경하면 됨.
- **만기환급금 자동 계산**: 동일 (`calculateMaturityRefund()`).
- **임시저장 (TEMP_SAVED)** (E1 예외 경로): enum 값만 있고 UI 미구현. 폼에 "임시저장" 버튼 + 백엔드 별도 엔드포인트(예: `POST /api/products?temp=true`) 추가 필요.
- **재산정 흐름** (Alternate A1: 요율 재산정 후 Basic Path 10 복귀): UI에서 "요율 재산정" 버튼 + 동일 폼 재제출.
- **특약 없음 체크박스** (Alternate A2): 폼에 체크박스 + 체크 시 특약 섹션 비활성화.

### 8.3 외부 시스템 (의도적 미구현)

- **금융감독원 API**: 호출 없음. CLAUDE.md "외부 API 미구현" 원칙. 인가 결과는 운영자가 페이지에서 "인가 승인 / 인가 불허 / 보완 요청" 버튼으로 직접 결정 (관리자가 금감원 역할도 겸한다고 간주). 추후 실 연동 시 `external/fss/` 패키지에 Mock → Real Client Adapter 패턴 도입 (LLM_WIKI §11).

### 8.4 파일 업로드

- claim 사고접수와 동일 수준: **파일명만 저장**. 실제 파일 바이트는 전송/저장 안 함. 정식 업로드 도입 시 백엔드에 Multipart 핸들러 + 파일 스토리지(S3 or local) + `*FileName` 컬럼 옆에 `*FilePath` 추가.

---

## 9. 로컬 실행 가이드

### 9.1 사전 요구사항

| 항목 | 필요 |
|---|---|
| JDK | 17 이상 (`pom.xml` `<release>17</release>`) — 본 머신은 JDK 22 사용 중 |
| Maven | IntelliJ 번들 사용 (`C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd`) — 또는 별도 설치 |
| Node.js | 18 이상 + npm |
| PostgreSQL 또는 Docker | 둘 중 하나 |

### 9.2 DB 컨테이너 (Docker 권장)

```powershell
# 처음만
docker run -d --name boonsan-pg -p 5432:5432 `
  -e POSTGRES_PASSWORD=1234 -e POSTGRES_DB=boonsan `
  postgres:16

# 다음부터는
docker start boonsan-pg
```

`application.properties` 기본값(`jdbc:postgresql://localhost:5432/boonsan`, `postgres`/`1234`)과 매칭.

### 9.3 백엔드 실행

PowerShell, 프로젝트 루트에서:
```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

또는 IntelliJ에서 `BoonsanApplication.java` 우클릭 → Run.

⚠️ **`Main.java` (콘솔 진입점) 실행 금지** — `com.boonsan.BoonsanApplication`이 진짜 엔트리. IDE Run config가 `Main`을 가리키면 새로 만들어야 함.

기동 확인: `http://localhost:8080/api/health` → `{"success": true, "message": "Boonsan API is running"}`

### 9.4 프론트 실행

```powershell
cd C:\Myongji_workspace\3y_1sem\boonsan\frontend
npm install   # 처음만
npm run dev
```

`http://127.0.0.1:5173/products/design` 접속.

### 9.5 DB 직접 확인

```powershell
# 전체 테이블
docker exec boonsan-pg psql -U postgres -d boonsan -c "\dt"

# 상품 목록
docker exec boonsan-pg psql -U postgres -d boonsan -c "SELECT product_code, product_name, insurance_type, product_status, created_at FROM product ORDER BY created_at DESC;"

# 인가 요청
docker exec boonsan-pg psql -U postgres -d boonsan -c "SELECT request_id, product_code, authorization_status, updated_at FROM product_authorization ORDER BY updated_at DESC;"

# 인터랙티브
docker exec -it boonsan-pg psql -U postgres -d boonsan
```

GUI를 원하면 IntelliJ Database 탭 또는 DBeaver로 `localhost:5432`, `postgres`/`1234`, `boonsan` 접속.

### 9.6 검증 시나리오 (E2E 1바퀴)

1. `/products/design`에서 상품 설계 저장 → 우측 카드에 `PRD-XXX-YYYY-NNNNNN` 발급
2. 발급된 코드 복사
3. `/products/authorization` 에서 해당 코드 조회 → "인가 요청 가능" 카드 표시
4. 인가 요청 폼에 사유 입력 + 첨부 파일명 선택 → 등록
5. 우측에 인가 상세 카드 + 결과 처리 패널 표시
6. "인가 승인" 클릭 → 상태가 `APPROVED`/`AUTHORIZED`로 갱신
7. DB에서 `product.product_status = AUTHORIZED`, `product_authorization.authorization_status = APPROVED` 확인

---

## 10. 향후 작업 가이드

### 10.1 새 유스케이스 추가 시 (예: 상품 단종)

1. `enums/`에 필요한 상태 추가 (없으면)
2. 모델 필요 시 확장 (skeleton 메서드 보존)
3. `schema.sql` 멱등 DDL 추가
4. `src/product/` 아래에 `dto/`, `mapper/`, `service/`, `controller/` 슬라이스 추가 — claim 패턴 그대로
5. XML 생성 + `mapper-locations=classpath*:mappers/**/*.xml`로 자동 로드되므로 별도 등록 불필요
6. 프론트: `types/product.ts`에 타입 추가 → `api/productApi.ts`에 함수 → `pages/product/`에 페이지 → `components/product/`에 컴포넌트 → `routes/AppRouter.tsx` 라우트 → `Sidebar.tsx` 메뉴 항목

### 10.2 텍스트 필드를 구조화 컬럼으로 분리할 때

권장 순서: **`rate_information` → `special_contract_info` → `main_coverage` → `subscription_conditions`** (Section 8.1 참조).

각 분리 작업은 1 PR 단위로 — schema migration → 모델 필드 분리 → DTO 분리 → Service 변환 로직 → 폼 필드 분리 순.

### 10.3 외부 금감원 API 연동 시 (Adapter 패턴)

LLM_WIKI §11 따라 `src/external/fss/` 생성:
- `FinancialSupervisoryClient` 인터페이스
- `MockFinancialSupervisoryClient` (현재 무동작)
- `RealFinancialSupervisoryClient` (실제 HTTP 호출)

`AuthorizationApplicationService`에 인터페이스 주입. `application.properties`로 활성 구현체 선택.

### 10.4 절대 하지 말 것

- `src/claim/**` 또는 `frontend/src/{api,components,pages,types}/claim*` 어떤 파일도 **수정 / 삭제 / 이름변경 금지**. 참고만 함.
- `model/insurance/FinancialSupervisoryService`에 외부 API 호출 코드 추가 금지.
- `Insurance` 추상 메서드(`calculatePremium`, `calculateMaturityRefund`) 또는 skeleton 빈 메서드를 **삭제** 금지 (구현은 가능).
- `Main.java` 또는 `src/service/` 옛 콘솔 코드 삭제 금지 (보존).
- PostgreSQL이 아닌 DB 문법 사용 금지.
- Controller에 비즈니스 로직 추가 금지.

---

## 11. 메타 — 본 작업의 기록

- 작업 브랜치: `WEB_PRODUCT` (이 문서 커밋 시 신규 생성, `origin/WEB`에서 분기)
- 기반 브랜치: `origin/WEB` (HEAD: `294213c chore: update wiki and sidebar navigation`)
- 추가/수정 파일 수: 백엔드 21, 프론트 13, 문서 1
- 검증: `mvn compile` 통과, `tsc --noEmit` 통과
- 미검증: 실제 DB 데이터 round-trip (사용자가 로컬 환경에서 진행 예정)
