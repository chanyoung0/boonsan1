# 클래스 다이어그램 보강 메모

> **목적**: `report.pdf` 44쪽 클래스 다이어그램에 빠져 있거나 코드/시나리오와 어긋나는 관계를 기록한다.  
> **원본 우선순위**: RFP > 유스케이스 다이어그램 > 시나리오 > 클래스 다이어그램 > 코드. 이 메모는 *코드/시나리오 측에서 발견된 다이어그램 결손*을 임시로 보존한다.  
> **반영 시점**: 보고서 클래스 다이어그램 차기 개정 시 이 항목들을 다이어그램에 흡수하고 본 메모는 삭제한다.

---

## 1. 다이어그램에 없는데 시나리오/코드에 필요한 관계

| 누락 관계 (전체 → 부분) | 근거 시나리오 / 코드 |
|---|---|
| **Contract → MaturityNotice** | 메뉴 5(만기계약 관리) — 만기 안내장 발송 흐름 |
| **Contract → Account** | 자동이체 정보(`autoTransferAccount`) — 모든 시나리오의 계약 상세 출력 |
| **Contract → Document** | 계약서/약관 등 계약 부속 문서 보관 (시나리오 보강용) |
| **AccidentReport → AccidentDocument** | 메뉴 6 — 사고경위서/진단서/청구서류 업로드 흐름 |
| **AccidentReport → Document** | 사고 관련 일반 문서 |
| **DamageInvestigation → PaymentApprovalDocument** | 메뉴 7 — 손해조사 중 지급품의서 작성 |
| **InsurancePayment → PaymentApprovalDocument** | 보험금 지급 승인 문서 보관 |
| **OutsourceRequest → Partner** | 메뉴 7 외부 위탁 — 협력업체 선택 |
| **Authorization → FinancialSupervisoryService** | 상품 인가 — 금융감독원 요청/응답 |
| **CompensationEvaluation → PaymentCollection** | 보상 평가 시 수납 데이터 참조 |
| **CompensationEvaluation → Transfer** | 보상 평가 중 이관 처리 |
| **Payout → DamageInvestigation** | 지급 결정 시 손해조사 결과 참조 |
| **Payout → InsurancePayment** | 지급 결정 → 보험금 지급 연결 |
| **UnderwritingHistory → AccidentHistory** | 심사 이력 안에 사고 이력 포함 |

## 2. 다이어그램의 Aggregation 방향이 어색한 항목

| 다이어그램 | 권장 |
|---|---|
| Contract → InsuranceApplication (Aggregation, 전체→부분) | 생애주기상 청약이 먼저 발생. *Association(단방향 참조)* 로 강등 권장. DB FK는 `contract.application_id`. |
| InsuredPerson → UnderwritingHistory (Aggregation) | 코드는 반대 방향(`UH.insuredPerson`). 양방향 또는 DB FK 기준으로 `UH → Person` 단방향이 자연. |

## 3. 다이어그램에 없는 클래스

| 클래스 | 사용처 |
|---|---|
| **Manager** (`model.person.Manager`) | `Transfer.assignee` 필드, 콘솔 출력의 사원번호/이름/부서 정보 보유 |

→ 보고서 차기 개정 시 클래스 다이어그램의 *계약/이관 영역* 또는 *조직 영역*에 추가 필요.

## 4. 다이어그램에 명시됐으나 코드 객체 참조로 미구현된 Aggregation (P2/P3에서 채울 예정)

다이어그램이 정의한 다음 Aggregation들은 현재 코드에서 ID/원시값으로만 연결되어 있다. P2(도메인 모델 정리) 단계에서 `List<T>` 또는 단일 객체 필드로 보강한다.

```
Insurance → Authorization
AccidentReport → DamageInvestigation
DamageInvestigation → OutsourceRequest, InsurancePayment
InsurancePayment → Objection, Subrogation
Subrogation → Objection
Endorsement → UnderwritingRequest
Reinstatement → UnderwritingRequest
Contract → Endorsement, Reinstatement, PaymentCollection,
           CompensationEvaluation, Payout, InsuranceApplication, Reinsurance
PaymentCollection → Transfer, UnpaidNotice
InsuredPerson → UnderwritingHistory
InsuranceApplication → Underwriting, Coinsurance
Coinsurance → Coinsurer
Underwriting → UnderwritingResult
UnderwritingResult → AccidentHistory
```

## 5. 사용자 확정 [참고 관계도]와의 차이

사용자가 별도 표로 제공한 [참고해야할 관계도]에는 다이어그램에 없던 다음 추가 관계가 명시되어 있다. 이는 위 1~3절을 통합한 결과이며, 코드 리팩토링(P2)은 이 표를 *상위 기준*으로 따른다.

- Contract ↔ InsuredPerson / Account / Payout / Reinsurance / InsuranceApplication / Document / MaturityNotice 등 14건
- InsuredPerson → Account
- AccidentReport → Document / DamageInvestigation
- DamageInvestigation → OutsourceRequest / InsurancePayment
- InsurancePayment → Objection / Subrogation / PaymentApprovalDocument
- Payout → DamageInvestigation / InsurancePayment
- Authorization → FinancialSupervisoryService
- CompensationEvaluation → PaymentCollection / Transfer
- Underwriting → Coinsurance / UnderwritingResult
- Coinsurance → Coinsurer
- UnderwritingHistory → AccidentHistory
- OutsourceRequest → Partner
- Document ▷ AccidentDocument / PaymentApprovalDocument (상속)
- Insurance ▷ AutoInsurance / FireInsurance / MarineInsurance (상속)
