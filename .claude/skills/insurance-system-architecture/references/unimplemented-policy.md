# 미구현 메서드 정책

다이어그램에는 있지만 아직 구현되지 않은 메서드들이 있다. 무조건 비우는 것도, 무조건 채우는 것도 아니다. 아래 기준으로 갈라서 처리한다.

## 1. 외부 연동 (영구히 비움)
외부 시스템에 직접 데이터를 보내거나 받는 동작만 해당한다. 이 프로젝트는 외부 API 연동을 하지 않으므로 빈 채로 둔다. 새로 채우려 하지 마라.

- `FinancialSupervisoryService.receiveAuthorizationRequest()` / `sendAuthorizationResult()` (금융감독원)
- `Authorization.sendAuthorizationRequest()` (금융감독원 전송)
- `Coinsurance.sendParticipationRequest()` (공동인수사 전송)
- `Coinsurer.registerResult()` (공동인수사 응답 수신)
- ICIS 신용정보 조회 (시나리오상 외부 API)

**판단 기준**: 메서드가 `send`/`receive`처럼 외부와 주고받는 것이면 외부 연동이다. 계산하거나 자기 필드를 바꾸는 것은 외부 연동이 아니다.

## 2. 계산 메서드 (공식대로 구현)
계산식은 두 종류로 나뉜다.

### (a) 시나리오에 공식이 있는 것 → 시나리오대로 구현
- `Coinsurance.calculateRetainedAmount()` — 보유액 = 보험가입금액 × 자사 보유 지분율
- `Coinsurance.allocatePremium()` — 각 사 배분액 = 보험료 × 각 사 지분율

### (b) 시나리오에 공식이 없는 것 → 아래 기본 공식 사용
요율표·계리 데이터가 과제 범위에 없으므로, 복잡한 계리 대신 단순 비례식을 기본값으로 정한다. 실제 요율이 생기면 이 상수만 교체한다.

| 메서드 | 기본 공식 | 비고 |
|--------|----------|------|
| `Insurance.calculatePremium()` | 보험료 = 보험가입금액 × 0.03 (기본요율 3%) | 상수 `BASE_RATE = 0.03` |
| `Insurance.calculateMaturityRefund()` | 만기환급금 = 납입보험료 합계 × 0.9 (환급률 90%) | 상수 `REFUND_RATE = 0.9` |
| `Reinsurance.calculatePremium()` | 재보험료 = 보험가입금액 × 출재비율(cessionRate) | 출재비율은 입력값 |
| `PaymentCollection.calculateLateFee()` | 연체료 = 미납금액 × 0.05 (연체율 5%) | 상수 `LATE_FEE_RATE = 0.05` |

이 계산들은 Service에 구현한다 (도메인이 아님). 예:
```java
// InsuranceProductService
private static final BigDecimal BASE_RATE = new BigDecimal("0.03");
public static BigDecimal calculatePremium(BigDecimal insuredAmount) {
    return insuredAmount.multiply(BASE_RATE);
}
```

## 3. 회계 처리 (비움)
회계장부 모듈이 있어야 의미가 있는 동작. 회계 모듈이 없으므로 비워둔다.

- `Reinsurance.recognizeAccounting()` (재보험료 계상)
- `Coinsurance.processCession()` (출재 계상)

## 4. 콘솔 환경이라 무대가 없는 것 (비움, 웹 단계 구현)
웹/배치에서 상태를 관리해야 의미가 있는 동작. 콘솔에선 실행 맥락이 없어 비워두되, 웹 단계에서 구현될 자리다.

- `Contract.executeContract()` / `renewContract()` / `terminateContract()`
- `AccidentDocument.uploadDocument()` (파일 업로드)

## 5. 다이어그램대로 분리해 구현 (테이블·Mapper·Service 생성)
현재 피보험자·심사이력·문서·이관 등의 정보가 메인 도메인에 객체 참조 필드로만 선언돼 있고, 실제로는 채우거나 저장하지 않는다. 클래스 다이어그램에는 별도 클래스로 그려져 있으므로, **다이어그램대로 분리해 테이블·Mapper·Service를 만든다.**

중요: 도메인 클래스는 이미 필드를 갖춘 채 존재하고, 다른 도메인이 이미 객체 참조 필드로 들고 있다(예: `Contract.insuredPerson`, `UnderwritingHistory.insuredPerson`, `Transfer.assignee`). 즉 **관계 구조는 이미 코드에 있다.** 빠진 것은 그 필드를 채우고·저장하고·조회하는 로직과 테이블·Mapper뿐이다. 관계를 새로 설계하지 말고 기존 필드를 활용한다.

### 분리·구현 대상과 순서
의존 관계상 참조되는 쪽(부모)부터 만든다.

1. **Account** (계좌) — 테이블 + Mapper + Service
   - 필드: accountHolder, accountNumber, accountType, bankName, balance
   - InsuredPerson, Contract가 참조하므로 가장 먼저.
2. **InsuredPerson** (피보험자) — 테이블 + Mapper + Service
   - 필드: name, residentRegistrationNumber, contact, accountInfo(→Account 참조)
   - 메인 도메인에 흩어진 피보험자 정보를 정규화하고, 심사·계약은 피보험자 ID로 참조.
3. **AccidentHistory** (사고이력) — 테이블 + Mapper + Service
   - UnderwritingHistory가 List로 가짐. UnderwritingHistory보다 먼저.
4. **UnderwritingHistory** (심사이력) — 테이블 + Mapper + Service
   - 피보험자별 과거 심사 기록 누적. InsuredPerson 참조(1:N), AccidentHistory List 보유.
5. **UnderwritingRequest / UnderwritingResult** (심사요청·결과) — 테이블 + Mapper + Service
   - Underwriting에 흡수돼 있던 요청·결과를 분리.
6. **Manager** (담당자) — 테이블 + Mapper + Service
   - 필드: employeeNo, name, department. Transfer가 assignee로 참조하므로 Transfer보다 먼저.
7. **Transfer** (이관) — 테이블 + Mapper + Service
   - 필드: assignee(→Manager 참조), transferredAt, transferType. PaymentCollection 흐름에서 분리.
8. **UnpaidNotice** (미납안내) — 테이블 + Mapper + Service
   - 필드: dueDate, paymentMethod, sentAt, unpaidAmount. PaymentCollection 흐름에서 분리.
9. **Document** (문서) — **한 테이블 + 타입 구분 컬럼** (single-table 상속)
   - `document_type` 컬럼으로 AccidentDocument / PaymentApprovalDocument를 구분. 하위 타입별 필드는 nullable로 같은 테이블에 둔다.
     - AccidentDocument 필드: checkDueDate, documentName, documentType, submissionStatus
     - PaymentApprovalDocument 필드: approvalStatus, approvedAt, approverEmployeeNo, damageAdequacyOpinion, lostIncomeAmount, medicalExpenseAmount, remarks, repairCostAmount, settlementAmount
   - Mapper XML의 resultMap에서 `document_type`에 따라 `<discriminator>`로 분기.

### 비움 유지 (분리 대상 아님)
- **Coinsurance, Coinsurer, Reinsurance** — 외부 연동(공동인수사/재보험사)이 필요하므로 1·3번 정책대로 비운다. 단 `Coinsurance.calculateRetainedAmount()` / `allocatePremium()` 등 계산 메서드는 2번 정책대로 공식으로 구현한다.

### 관계 설정
- 외래키로 도메인 간 참조를 연결한다. 예: `UnderwritingHistory.insured_person_id → InsuredPerson`, `AccidentHistory.history_id → UnderwritingHistory`, `InsuredPerson.account_id → Account`, `Transfer.manager_employee_no → Manager`.
- 기존에 메인 도메인 하나에 다 넣어 저장하던 Service/Console 흐름(특히 `UnderwritingService`, `PaymentCollectionService`)을, 분리된 도메인을 각각 저장하도록 수정한다.

### 주의
- 이 작업은 잘 동작하던 흐름을 건드리므로, 도메인 하나씩 (위 순서대로) 테이블→Mapper→Service→호출부 순으로 만들고 그때마다 동작을 확인한다.
- `InsuranceApplication`은 이미 Mapper·Service가 있으므로 새로 만들지 않는다.
- `Account.checkBalance()`처럼 이미 구현됐고 자기 필드만 보는 메서드는 그대로 도메인에 둔다.
- `mybatis-config.xml`에 새 Mapper XML을 모두 등록한다. `schema.sql`에 새 테이블을 모두 추가한다.

### 작업 분할 시 주의사항 (세션을 나눠 진행할 때)
- 1:1 이름 정렬(예: `registerAccidentReport`)은 이미 그 이름으로 구현된 경우가 대부분이다. 새로 rename하지 말고 "이미 맞는지 확인만" 한다.
- 계산 메서드를 구현할 때 `Reinstatement.calculateUnpaidPremium()`, `UnpaidNotice.calculateUnpaidAmount()`도 빠뜨리지 않는다 (미납 관련 계산).
- `Transfer`와 `UnpaidNotice`는 둘 다 `PaymentCollectionService`를 수정하므로 같은 세션에서 함께 작업한다 (다른 세션으로 나누면 같은 Service를 두 번 건드려 충돌 위험).
- `Subrogation`은 `SubrogationService`에 DB 저장 로직이 없다 (기존 메서드는 계산·객체생성만 함). 저장 메서드를 신규 추가해야 한다. 자세한 절차는 `subrogation-main-integration.md` 참고.

## 요약
외부 연동(1번)·회계(3번)는 비우고, 계산(2번)은 공식대로 구현하며, 4번은 웹 단계 구현 자리로 남겨둔다. 5번은 다이어그램대로 테이블·Mapper·Service를 분리해 구현한다: Account, InsuredPerson, AccidentHistory, UnderwritingHistory, UnderwritingRequest/Result, Manager, Transfer, UnpaidNotice, Document(타입 구분 한 테이블). 단 Coinsurance·Coinsurer·Reinsurance는 외부 연동이라 비우고 계산 메서드만 구현한다.
