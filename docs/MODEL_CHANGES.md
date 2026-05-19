# 모델 변경 이력 (DB 전환 과정)

기존 모델에는 콘솔이 입력으로 받아왔지만 저장 위치가 없던 필드들이 있다.
DB 전환에 맞춰 모델에 최소한의 필드만 추가했고, 그 내역을 이 문서에 모은다.

추가 원칙
- 콘솔이 이미 입력받고 있어 DB 저장 의미가 명확한 필드만 추가한다.
- 기존 생성자 시그니처는 깨지 않는다. 새 필드는 setter 로만 접근한다.
- enum 폴백, null 허용 정책은 partner/compensation_evaluation DBO 와 동일하게 유지한다.

---

## accident_report 영역

### `model.accident.AccidentReport`
| 신규 필드 | 타입 | 사유 |
| --- | --- | --- |
| `policyNumber` | `String` | AccidentReportConsole 에서 "보험 증권번호" 로 입력받지만 모델에 저장 위치가 없었음. DB 컬럼 `policy_number` 와 대응. |
| `accidentAt` | `LocalDateTime` | 콘솔에서 "사고 일시" 로 입력받지만 저장 위치 없음. `createdAt` 은 접수 시각이라 별도 컬럼 필요. DB 컬럼 `accident_at` 과 대응. |

### `accident_report` 테이블
| 추가 컬럼 | 타입 |
| --- | --- |
| `policy_number` | `VARCHAR(50)` |
| `accident_at` | `TIMESTAMP` |

---

## 계약 영역

계약 영역 모델은 대부분 PK 가 없었고 어느 계약(`policyNumber`) 에 속하는지 추적하는 필드도 없었다.
DB 전환을 위해 각 모델에 PK 와 FK 역할을 할 식별자 컬럼을 추가했다.

### `model.contract.Payout`
| 신규 필드 | 타입 | 사유 |
| --- | --- | --- |
| `payoutId` | `String` | PayoutService 가 메모리 Map (`payoutMap`) 으로 보관하던 식별자. DB 에서는 PK 컬럼. |
| `policyNumber` | `String` | PayoutService 가 메모리 Map (`payoutPolicyNumberMap`) 으로 보관하던 증권번호. DB FK. |
| `cancelled` | `boolean` | PayoutService 가 메모리 Set (`cancelledPayoutIdSet`) 으로 보관하던 취소 상태. `cancelPayment()` 가 직접 갱신. |
| `rejectionReason` | `String` | 반려 사유. 콘솔에서 입력받지만 저장 위치가 없었음. |

PayoutService 의 4개 정적 컬렉션 (`payoutMap`, `contractMap`, `payoutPolicyNumberMap`, `cancelledPayoutIdSet`) 은 제거하고 `PayoutDBO` 로 위임.

### `model.contract.Endorsement` / `Reinstatement` / `PaymentCollection` / `MaturityNotice`
| 신규 필드 | 타입 | 사유 |
| --- | --- | --- |
| `<엔티티명>Id` | `String` | 행 식별자 (PK) — 기존 모델에 PK 가 없었음. |
| `policyNumber` | `String` | 어느 계약에 대한 처리인지 추적. DB 컬럼은 `contract(policy_number)` FK. |

### `model.contract.Transfer` / `UnpaidNotice`
| 신규 필드 | 타입 | 사유 |
| --- | --- | --- |
| `<엔티티명>Id` | `String` | 행 식별자 (PK). |
| `paymentCollectionId` | `String` | Aggregation 상위인 PaymentCollection 추적. DB FK. |

### 영향 받은 테이블
- `payout` (신규)
- `endorsement` (신규)
- `reinstatement` (신규)
- `payment_collection` (신규)
- `maturity_notice` (신규)
- `transfer` (신규)
- `unpaid_notice` (신규)
- `contract` (신규, 기존 `policyNumber` 가 PK)

---

## 언더라이팅 영역

언더라이팅 모델은 InsuranceApplication 을 제외하고 모두 PK 가 없었다.

### 추가된 PK / FK
| 모델 | 신규 필드 | 사유 |
| --- | --- | --- |
| `Underwriting` | `underwritingId` (PK), `applicationId` (FK → InsuranceApplication) | 심사 행 식별 + 어떤 청약에 대한 심사인지 |
| `UnderwritingRequest` | `requestId` (PK), `policyNumber` (FK → contract) | 배서/부활 등 어떤 계약에 대한 심사 요청인지 |
| `UnderwritingResult` | `resultId` (PK), `underwritingId` (FK → underwriting) | 심사 결과 식별 + 어떤 심사의 결과인지 |
| `UnderwritingHistory` | `historyId` (PK), `insuredPersonId` (FK → insured_person) | 누구의 심사 이력인지 |
| `Coinsurance` | `coinsuranceId` (PK), `applicationId` (FK → insurance_application) | 공동인수 행 식별 + 어떤 청약의 공동인수인지 |
| `Coinsurer` | `coinsurerId` (PK), `coinsuranceId` (FK → coinsurance) | 공동인수에 참여하는 보험사 식별 |
| `Reinsurance` | `reinsuranceId` (PK) | 한 계약에 여러 재보험 가능하므로 별도 PK. 기존 `contractId` 는 FK 역할로 유지 |
| `InsuranceApplication` | (변경 없음) | 이미 `applicationId` 가 PK |

### 영향 받은 테이블
- `underwriting`, `underwriting_request`, `underwriting_result`, `underwriting_history`
- `coinsurance`, `coinsurer`, `reinsurance`
- `insurance_application`
