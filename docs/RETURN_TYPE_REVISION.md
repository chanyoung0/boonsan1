# Return Type Revision Guide

이 문서는 `docs/original`에 보관된 원본 클래스 다이어그램의 보완 문서이다.

원본 클래스 다이어그램에는 일부 메서드 반환형이 `void`로 작성되어 있으나, 이후 설계 검토 과정에서 아래 메서드들은 반환값이 반드시 필요하다고 확정되었다. 따라서 아래 표의 반환형은 공식 설계 보완사항으로 취급하며, 원본 클래스 다이어그램의 `void` 표기보다 우선한다.

## 적용 원칙

- 원본 유스케이스 다이어그램, 유스케이스 시나리오, 클래스 다이어그램을 최우선 기준으로 한다.
- 단, 아래 목록에 포함된 메서드는 보완된 반환형을 우선 적용한다.
- 구현 편의성을 이유로 반환형을 임의 변경하지 않는다.
- 아래 반환형은 수정된 클래스 다이어그램 기준의 확정 사항이다.

## 확정 반환형 목록

| No. | Class | Method | 원본 반환형 | 보완 반환형 |
| --- | --- | --- | --- | --- |
| 1 | `AccidentHistory` | `getAccidentHistory()` | `void` | `List<AccidentHistory>` |
| 2 | `AutoInsurance` | `getAccidentHistory()` | `void` | `List<AccidentHistory>` |
| 3 | `Coinsurer` | `getResult()` | `void` | `ApprovalStatus` |
| 4 | `Underwriting` | `calculateScore()` | `void` | `float` |
| 5 | `CompensationEvaluation` | `analyzeDamageAmount()` | `void` | `String` |
| 6 | `Subrogation` | `retrievePaymentDetails()` | `void` | `InsurancePayment` |
| 7 | `Contract` | `checkPaymentStatus()` | `void` | `boolean` |
| 8 | `InsuranceApplication` | `receiveApplication()` | `void` | `String` |
| 9 | `Account` | `checkBalance()` | `void` | `boolean` |
| 10 | `UnderwritingHistory` | `getHistory()` | `void` | `List<UnderwritingHistory>` |
| 11 | `DamageInvestigation` | `requestFraudInvestigation()` | `void` | `RequestStatus` |

## 확인 필요 후보

아래 항목은 수정 클래스 다이어그램에서 반환형 보정 가능성이 확인되었으나, 아직 확정 항목으로 취급하지 않는다.
구현 전에 사용자 확인 또는 설계 자료 재확인이 필요하다.

| No. | Class | Method | 현재 코드 반환형 | 확인 필요 반환형 후보 |
| --- | --- | --- | --- | --- |
| 1 | `InsurancePayment` | `transfer()` | `void` | `PaymentStatus` |

## 구현 시 주의사항

- 위 반환형은 설계 보완 결과이므로 코드 구현 시 우선 기준으로 삼는다.
- 이 문서에 없는 메서드 반환형은 기존 원본 설계 자료와 현재 코드 구조를 기준으로 판단한다.
- 불확실한 반환형은 임의로 변경하지 않고 설계 자료 또는 사용자 확인을 거친다.
