# CLAUDE.md — 계약관리(contract) 웹 전환 작업 지침

> 이 문서는 Claude Code가 **계약관리(contract) 도메인**을 웹으로 구현할 때 따르는 작업 지침이다.
> 작업자: 찬영 (계약관리 담당). 상품관리(product)는 승완, 청약심사(underwriting)는 기현이 별도로 진행한다.

---

## 0. 가장 먼저 읽을 것 / 기준 우선순위

작업 전 반드시 아래 순서로 컨텍스트를 확보한다.

1. **`docs/LLM_WIKI.md`** — 프로젝트 전체 아키텍처/규칙의 최우선 기준. 이 문서와 충돌하면 LLM_WIKI를 따른다.
2. **기존 `claim` 모듈 코드** — 이번 작업의 **실제 레퍼런스 템플릿**. 새 패턴을 발명하지 말고 claim을 그대로 복제한다.
   - 백엔드: `src/claim/{controller,service,mapper,dto}`, `src/main/resources/mappers/claim/*.xml`
   - 프론트: `frontend/src/{pages,components,api,types}/claim`
3. 신동아화재 RFP / 중간 리포트 / 유스케이스 다이어그램·시나리오 / 클래스 다이어그램 (도메인 의미 확인용)

> 도메인 메서드 판단 시: 클래스 다이어그램의 도메인 메서드를 웹 코드에 반영할지/이동할지 판단하거나, model의 빈 메서드(`calculateLateFee`, `calculateUnpaidPremium`, `executeContract` 등)를 채울지 비울지 판단할 때는 **`insurance-system-architecture` 스킬**(`.claude/skills/insurance-system-architecture/`)을 참고한다. 특히 분납/수금의 연체료·부활의 미납보험료·제지급금 계산 공식이 거기 있다.

### 주의: 낡은 정보 무시
- 이 레포에 **콘솔(text-base) 시절의 옛 설명**이 남아 있을 수 있다(예: "웹/DB 없이 콘솔에서 동작", "repository는 List로 메모리 저장", `Main.java` 콘솔 메뉴, `src/service/contract/*Service.java`의 static `run()`).
- 현재 단계는 **웹(Spring Boot + MyBatis + PostgreSQL + React)**이다. 콘솔 관련 지침은 따르지 않는다.
- 단, `src/service/`, `Main.java`, `common/ConsoleUtil` 등 **기존 콘솔 코드는 삭제·변경하지 않는다**(LLM_WIKI: 기존 구조 임의 삭제 금지). 그냥 건드리지 말고 둔다.

---

## 1. 핵심 규칙 (Non-Negotiable)

- 첫 작업은 **분석과 보고**다. 사용자 승인 전 코드 수정 금지 (LLM_WIKI 16번).
- 기존 클래스명/메서드명/패키지명을 임의 변경·삭제하지 않는다.
- **Controller에 업무 로직 금지.** Controller는 얇은 HTTP 어댑터.
- 업무 로직·상태 전이·검증·ID 채번은 **Service**에서만.
- DB 접근은 **Mapper(interface) + XML**에서만.
- **Request DTO / Response DTO를 분리**한다. Model/Entity를 컨트롤러에서 그대로 JSON 반환 금지.
- 프론트 Page에서 직접 fetch 금지. **`api/contractApi.ts` 모듈 경유.**
- 외부 연동(있을 경우)은 Service에 하드코딩하지 말고 `external` 어댑터로 분리, 초기엔 Mock.
- 불확실하면 임의 판단하지 말고 사용자(찬영)에게 질문한다.

---

## 2. claim 레퍼런스 패턴 (이대로 복제할 것)

claim 모듈을 분석한 결과, 아래 패턴이 **불변 규약**이다. contract도 동일하게 따른다.

### 2.1 레이어 흐름
```
React Page → api/contractApi.ts → Controller → ApplicationService(@Transactional)
  → Mapper(interface) + XML → PostgreSQL
  → Response DTO → ApiResponse<T> → JSON → React State
```

### 2.2 Controller 규약
- `@RestController`, 클래스 레벨 `@RequestMapping("/api/contracts...")`.
- 메서드는 service 호출 후 **`ApiResponse.success(data, "메시지")`**로 래핑해 반환.
- 입력은 `@Valid @RequestBody`, 경로 변수는 `@PathVariable`.
- 조회 단건 없음 → service가 `NoSuchElementException` 던지게 두고 Controller는 try-catch 하지 않는다.

### 2.3 ApplicationService 규약
- `@Service`. 조회는 `@Transactional(readOnly = true)`, 변경은 `@Transactional`.
- 입력 정규화 헬퍼: `requireText(value, fieldName)` (null/blank면 `IllegalArgumentException`).
- 없으면 `NoSuchElementException("... not found: " + key)`.
- 상태 전이는 "현재 상태 검증 → update → 변경 0건이면 예외" 패턴.
- ID 채번 패턴: `PREFIX-YYYY-NNNNNN` (예: 배서 `END-2026-000123`, 심사요청 `REQ-2026-...`).
  `ThreadLocalRandom.current().nextInt(1, 1_000_000)` + `String.format("%06d", seq)` + `Year.now().getValue()`.

### 2.4 Mapper + XML 규약
- interface 메서드는 `@Param("...")`으로 인자 명시.
- XML namespace = mapper 인터페이스 FQN. `resultMap`으로 컬럼↔필드 매핑.
- enum 컬럼은 `javaType="enums.XxxStatus"` 명시.
- "최신 1건" 조회는 `ORDER BY created_at DESC LIMIT 1` 패턴 사용.
- 매퍼 XML 위치: `src/main/resources/mappers/contract/*.xml`.

### 2.5 공통 인프라 (이미 존재 — 재사용만)
- `common.ApiResponse<T>` : `success(data)`, `success(data, message)`, `failure(message, errorCode)`.
- `exception.GlobalExceptionHandler` : ValidationException→400, IllegalArgumentException→400(INVALID_REQUEST), NoSuchElementException→404(NOT_FOUND).
  **새 예외 클래스를 만들 필요 없다. 위 표준 예외를 던지면 된다.**

### 2.6 프론트 규약
- `api/apiClient.ts`의 `apiRequest<T>`가 `ApiResponse`를 언래핑(`body.data` 반환, 실패 시 `ApiError`).
- `types/contract.ts`에 백엔드 Response DTO와 1:1 대응 타입 정의.
- `api/contractApi.ts`에 함수형 API 호출 모음 (claimApi.ts와 동일 스타일).
- page는 `pages/contract/`, 컴포넌트는 `components/contract/`.

---

## 3. 계약관리(contract) 도메인 범위

### 3.1 유스케이스 (중간 리포트 / 시나리오 기준)
구현 대상 5개 + 1개:
1. **배서를 관리한다** (Endorsement) — 계약 내용 변경, 심사 필요 시 `<<include>> 심사를 요청한다`
2. **부활을 관리한다** (Reinstatement) — 실효 계약 재활성화, `<<include>> 심사를 요청한다`
3. **제지급금을 관리한다** (Payout)
4. **분납/수금을 관리한다** (PaymentCollection) — 미납안내(UnpaidNotice)/이관(Transfer) 보조
5. **만기계약을 관리한다** (MaturityContract) — 만기안내(MaturityNotice)
6. **심사를 요청한다** (UnderwritingRequest) — 배서/부활에서 호출. **주체는 U/W(언더라이터)**.

> 주의: "심사를 요청한다"는 청약심사(기현) 도메인과 경계가 맞닿는다. 심사 로직 자체(Underwriting 판단)는 기현 영역이다. contract 쪽은 **심사 요청 생성/상태 추적**까지만 책임진다. 경계가 모호하면 구현 전에 사용자에게 질문한다.

### 3.2 기존 model (이미 존재, 콘솔 stub 상태 — 필요한 필드/getter/setter는 채워도 됨)
`src/model/contract/` : `Contract`, `Endorsement`, `Reinstatement`, `PaymentCollection`, `Payout`, `Transfer`, `UnpaidNotice`, `MaturityNotice`, `CompensationEvaluation`
`src/model/contract/UnderwritingRequest`는 `src/model/underwriting/UnderwritingRequest.java`에 있음(주의: underwriting 패키지).
- 이 model들은 빈 메서드 stub(`changeContractStatus()` 등)이 있다. **stub 메서드는 그대로 두고**, MyBatis 매핑에 필요한 필드/getter/setter만 채운다. (product 담당 승완이 `model/insurance/*`를 이런 식으로 채운 선례가 있음.)

### 3.3 enum (이미 존재 — 재사용)
- `ContractStatus` : ACTIVE, TERMINATED, SUSPENDED, EXPIRED, PENDING
- `EndorsementType` : COVERAGE_CHANGE, BENEFICIARY_CHANGE, PREMIUM_CHANGE, SPECIAL_CONTRACT_CHANGE
- `RequestStatus` : PENDING, IN_PROGRESS, COMPLETED, CANCELLED
- `PaymentCycle`, `PaymentType`, `PaymentStatus`, `TransferType`, `ReinstatementReason`, `ChangeReason`, `DeliveryMethod` 등도 존재. 부족하면 추가하되 기존 enum 변경 금지.

### 3.4 API 경로 (LLM_WIKI 9번 기준, claim 스타일로 확정)
```
GET    /api/contracts/{policyNumber}                         계약 조회
POST   /api/contracts/{policyNumber}/endorsements           배서 신청
POST   /api/contracts/{policyNumber}/reinstatements         부활 신청
POST   /api/contracts/{policyNumber}/payouts                제지급금
POST   /api/contracts/{policyNumber}/payment-collections    분납/수금
POST   /api/contracts/{policyNumber}/maturity               만기계약
POST   /api/contracts/{policyNumber}/.../underwriting-request  심사 요청
```
- 실제 경로는 시나리오 분석 후 사용자 승인받아 확정한다.

### 3.5 데이터 모델링 결정 (★ 반드시 따를 것)

현재 웹으로 이관된 도메인은 claim(보상처리)뿐이며, contract 도메인은 테이블·Mapper·Service·Controller가 **하나도 없다**(model 클래스 껍데기만 존재). 따라서 contract는 거의 전부를 claim 패턴으로 새로 만든다.

정규화 수준은 다음으로 **확정**한다:

1. **Contract(계약)는 독립 테이블 + Mapper로 정식 구현한다.**
   - 배서/부활/제지급금/분납수금/만기는 모두 "이미 존재하는 계약을 조회·검증"하는 후속 작업이다. 계약 테이블이 없으면 매달릴 대상이 없다.
   - 각 처리(Endorsement/Reinstatement/Payout/PaymentCollection/MaturityNotice)는 별도 테이블로 만들고 `contract`(policy_number)를 FK로 참조한다. claim에서 `damage_investigation`이 `accident_report`를 참조하는 구조와 동일하게 간다.
   - 계약관리 유스케이스 시나리오의 "해당 증권번호로 계약정보를 조회하여 출력한다" 단계는 실제로 contract 테이블을 조회해 상태(ACTIVE/실효/만기 등)를 검증하는 것으로 구현한다. claim이 policy_number를 문자열로만 받고 검증 안 한 방식을 contract에서는 따르지 않는다.

2. **피보험자(InsuredPerson)·계좌(Account)는 지금 단계에서 별도 테이블로 분리하지 않는다 (비정규화).**
   - claim과 일관되게, 필요한 피보험자/계좌 정보는 contract(또는 해당 처리) 테이블의 **컬럼으로 직접** 둔다.
   - 이유: 피보험자/계좌는 청약심사(기현)도 사용하는 공용 도메인이라, 지금 양쪽이 각자 테이블/Mapper를 만들면 머지 충돌이 난다. 소유권이 정해지기 전까지 비정규화로 둔다.
   - **단, 나중에 분리(정규화) 리팩터링이 쉽도록 컬럼명을 명확히 둔다.** 예: `insured_name`, `insured_rrn`, `insured_contact`, `account_number`, `account_bank`. 뭉뚱그린 이름 금지.
   - `model/person/InsuredPerson.java`, `Account.java`는 콘솔 껍데기로 두고, 웹 Mapper/테이블을 만들지 않는다.

3. **분리(정규화)는 "추후 리팩터링"으로 남긴다.**
   - 셋이 머지한 뒤, 필요·여유가 있으면 피보험자/계좌를 별도 테이블로 분리하고 contract는 FK만 남기는 리팩터링을 한다. 이는 단순 추가가 아니라 컬럼 제거+테이블 신설+데이터 이전+Mapper/Service/DTO/프론트 수정이 따르는 작업임을 인지한다.
   - 분리를 하지 않고 비정규화로 마감해도 "프로젝트 전체가 claim 패턴으로 통일됨"으로 정당화된다.

---

## 4. 충돌 방지 규칙 (★ 머지 위해 매우 중요)

3명이 병렬 작업 후 합친다. 아래 **공유 파일은 "추가(append)"만** 하고 기존 줄은 절대 수정/삭제하지 않는다.
(claim→product 전환 때 승완이 이 규칙을 지켜 충돌이 거의 없었다. 동일하게 한다.)

| 파일 | 추가 방식 |
|------|-----------|
| `src/com/boonsan/BoonsanApplication.java` | `scanBasePackages`에 `"contract"` 한 줄 추가 |
| `src/config/MyBatisConfig.java` | `@MapperScan` 배열에 `"contract.mapper"` 추가 |
| `src/main/resources/schema.sql` | 파일 **맨 끝**에 contract 테이블 DDL 추가 (기존 테이블 손대지 않음) |
| `frontend/src/routes/AppRouter.tsx` | contract 라우트 import + `<Route>` 추가 |
| `frontend/src/components/layout/Sidebar.tsx` | contract 메뉴 경로 매핑 추가 |
| `src/config/WebConfig.java` | CORS는 이미 `/api/**` 전체 허용 → **건드릴 필요 없음** |

- 새 파일은 전부 `contract` 패키지 / `contract` 폴더 안에서만 생성한다.
- claim, product 패키지/파일은 **읽기만(레퍼런스)**, 절대 수정하지 않는다.
- schema.sql 테이블은 claim/product 패턴대로 `CREATE TABLE IF NOT EXISTS` + `ALTER TABLE ... ADD COLUMN IF NOT EXISTS` 형태를 따른다.

---

## 5. 작업 절차

### Before Work (첫 응답은 반드시 분석만)
사용자 승인 전 코드 수정 금지. 첫 작업으로 아래를 보고한다:
1. claim 모듈의 정확한 레이어 구조 요약 (복제할 패턴)
2. contract 도메인에서 만들 파일 목록 (controller/service/mapper/xml/dto/model/enum, 프론트 page/component/api/types)
3. 6개 유스케이스 중 **가장 먼저 구현하기 좋은 후보**와 이유
4. 손대야 할 공유 파일 6개 + 각각 어떤 줄을 추가할지
5. 경계가 모호한 부분(특히 "심사를 요청한다" ↔ 기현의 underwriting) 질문 목록

### During Work
- 유스케이스 **하나씩** 수직 슬라이스(Controller→Service→Mapper→XML→DTO→프론트)로 완성.
- 한 슬라이스 끝나면 빌드/타입체크 확인 후 다음으로.
- claim의 동일 위치 파일을 열어 패턴을 그대로 맞춘다.

### After Work
- 변경/추가 파일 목록 제시.
- 어떤 유스케이스에 매핑되는지 설명.
- `mvn -q -DskipTests compile` (백엔드) / `npm run build`(프론트) 결과 확인.
- 공유 파일은 "추가만 했는지" 자가 점검.

---

## 6. 빌드/실행 참고
- 백엔드: Spring Boot 3.3.6 / Java 17 / MyBatis 3.0.4 / PostgreSQL. `pom.xml`의 `sourceDirectory`는 `src` (표준 `src/main/java` 아님).
- 로컬 DB: `application.properties` 기본값 `jdbc:postgresql://localhost:5432/boonsan` (env로 override). `schema.sql`은 `spring.sql.init.mode=always`로 기동 시 실행.
- 프론트: React 18 + Vite + TS. `npm run dev`(127.0.0.1:5173), API base는 `VITE_API_BASE_URL` 또는 `localhost:8080`.
- 배포: Frontend=Vercel, Backend=Render, DB=Supabase (참고용, 로컬 개발엔 무관).
