# DBO 시그니처 개선 권고 메모

> **컨텍스트**: `MyBatis_migration_1st` 브랜치에서 13개 활성 DBO의 본문을 MyBatis Mapper 호출로 교체하면서 *시그니처는 그대로 유지*했다. 그 결과 기존 구조에서 끌고 온 설계 이슈가 그대로 남아 있다. 본 메모는 다음 PR에서 다룰 후속 정리 항목을 정리한다.
>
> **원칙**: 한 PR에 하나의 관심사. 본 PR은 "파이프라인 교체"였고, 이 메모는 "API 정리"를 위한 별도 PR의 가이드라인이다.

---

## 1. `save(M model)` 시그니처가 명목상 존재하지만 즉시 throw

다음 7개 DBO는 `save(M)` / `update(M)`을 선언하지만 즉시 `UnsupportedOperationException`을 던진다.

| DBO | 누락 파라미터 |
|---|---|
| `UnderwritingDBO` | `underwritingId, empNo, empName, empDept, uwResult` |
| `InsuranceApplicationDBO` | `policyNumber, applicationStatus, appliedCondition` |
| `EndorsementDBO` | `endorsementId, policyNumber, endorsementTypeChoice, changeReason, uwResult` |
| `ReinstatementDBO` | `reinstatementId, policyNumber, uwResult, reinstatementStatus` |
| `PaymentCollectionDBO` | `collectionId, policyNumber, collectionStatus, transferType` |
| `MaturityNoticeDBO` | `noticeId` |
| `AccidentReportDBO` | `policyNumber, accidentStatus, documentSubmissionStatus, accidentAtText` |
| `DamageInvestigationDBO` | `reportNo, investigationStatus` |
| `PayoutDBO` | `payoutId, policyNumber, payoutStatus` |

### 문제점
- **다형성 깨짐**: 공통 인터페이스를 약속하지만 런타임 폭탄.
- **컴파일러가 도와주지 못함**: 잘못된 시그니처로 호출해도 컴파일 시점에 감지되지 않음.
- **호출자가 DB 컬럼을 알아야 함**: Service 계층이 schema 컬럼 이름 단위로 결합됨.

### 권장 해결안 A — 파라미터 객체 도입
각 엔티티별 `XxxSaveCommand` DTO를 만들어 *불변 파라미터 묶음*으로 전달.

```java
// 예: UnderwritingSaveCommand
public final class UnderwritingSaveCommand {
    public final Underwriting underwriting;
    public final String id;
    public final String empNo, empName, empDept;
    public final String uwResult;
    // ...
}

// DBO
public boolean save(UnderwritingSaveCommand cmd) { ... }
```

### 권장 해결안 B — 모델 보강
누락된 필드를 모델에 추가하여 `save(M model)`만 남긴다.
- `Underwriting` ← `empNo, empName, empDept` 필드 추가 + 신규 enum `UnderwritingResultCode`
- `AccidentReport` ← `policyNumber, accidentStatus(상태용), documentSubmissionStatus, accidentAtText` 필드 추가
- `Endorsement` ← `changeReason, uwResult` 필드 추가
- 등등

해결안 B가 **클래스 다이어그램과 일치하는 방향**이다. 다이어그램은 이미 이런 필드를 엔티티의 일부로 보고 있다.

---

## 2. 모델 필드 타입과 DB 컬럼 의미가 불일치

| 모델 필드 | 타입 | DB 컬럼 | 문제 |
|---|---|---|---|
| `AccidentReport.accidentStatus` | `AccidentDetailsType` (VEHICLE/PROPERTY/INJURY/NATURAL_DISASTER) | `accident_status` (RECEIVED/DOCUMENT_PENDING/INVESTIGATION_REQUIRED/REJECTED) | **사고 종류와 사고 접수 상태가 같은 이름을 공유** — 모델 필드는 사용되지 않고 DBO 파라미터로 우회 |
| `Endorsement.changeReason` | `ChangeReason` enum (INSURED_AMOUNT_CHANGE 등) | `change_reason` TEXT (사용자 자유 입력) | 모델 enum 값은 사용되지 않고 DBO 파라미터로 별도 String 전달 |
| `AccidentDetailsType` + `AccidentType` | 두 enum 모두 존재 (VEHICLE/PROPERTY/INJURY 유사) | — | 중복·혼란 |

### 권장
- 새로운 `enums.AccidentReportStatus`(RECEIVED/DOCUMENT_PENDING/INVESTIGATION_REQUIRED/REJECTED) 도입
- `AccidentReport.accidentStatus`를 `AccidentReportStatus`로 재정의
- `AccidentDetailsType`는 별도 `accidentDetailType` 필드(또는 삭제)
- `AccidentDetailsType`와 `AccidentType` 중 하나로 통일

---

## 3. fetched 객체의 부분 데이터 (양방향 매핑 깨짐)

현재 `mapXxx()` (이제는 mapper XML의 resultMap)이 모델에 없는 컬럼(`policy_number`, `accident_status` 등)을 모델 객체에 set하지 않음. 그 결과:

- `findById(id)`로 가져온 객체는 핵심 정보가 빠져 있음
- `XxxDBO.findStatusById(id)`, `findPolicyNumberById(id)` 같은 *컬럼 단위 단건 조회 API*가 Service에 노출됨
- Service에서 객체로 한 번에 다루지 못하고 SQL을 N번 날리게 됨

### 권장
§1 권장안 B를 적용해 모델에 필드 보강 → resultMap에 매핑 추가 → 컬럼 단위 API 제거.

---

## 4. 정규화/검증 로직이 DBO에 잔존

다음 헬퍼 메서드는 영속성과 무관한 비즈니스 변환 로직인데 DBO 내부에 있음 (MyBatis 전환 후에도 그대로 남김):

- `EndorsementDBO.resolveUwResultName()`: 한글 "할증" → `"SURCHARGE"` 변환
- `EndorsementDBO.resolveEndorsementTypeChoice()`: 콘솔 메뉴 선택값 `"1"/"2"/"3"/"4"`를 enum으로 매핑
- `AccidentReportDBO.resolveAccidentStatus()`: 화이트리스트 외에 `"RECEIVED"` 강제
- `UnderwritingDBO.resolveResultName()`, `ReinstatementDBO.resolveUwResultName/StatusName()`, `PaymentCollectionDBO.resolveCollectionStatus/TransferType()`, `MaturityNoticeDBO.resolveDeliveryMethodName/RenewalIntention()`, `CompensationEvaluationDBO.resolveXxxName()`, `PayoutDBO.resolvePayoutStatus/PaymentTypeName/CalculationBasisName()`

### 권장
- 한글 ↔ enum 변환: Service 또는 Controller(콘솔)에서 enum 객체로 변환 후 모델에 set
- 화이트리스트 기본값 부여: enum 객체로 강타이핑하면 자연스럽게 해결
- DBO는 *오로지 영속성만* 담당

---

## 5. enum 처리: 기본 fallback의 silent masking

기존 DBO는 DB에서 알 수 없는 enum 값을 읽으면 *조용히 기본값으로 fallback*했다. MyBatis로 전환하면서 기본 `EnumTypeHandler`(`Enum.valueOf`)를 사용하기 때문에 잘못된 값은 **즉시 `IllegalArgumentException`을 던진다**.

### 변경된 동작
- 이전: DB에 잘못된 값 → `PENDING`/`AVERAGE` 등 기본값 반환
- 현재: DB에 잘못된 값 → `IllegalArgumentException` (DBO에서 catch하고 `[DB 오류]` 출력 후 `null` 반환)

### 평가
- **개선임**: schema 제약을 우회한 비정상 데이터를 일찍 발견할 수 있음
- 운영 가드: 마이그레이션 직후 schema 위반 데이터가 있는지 확인 필요
- 정말 fallback이 필요하다면 별도 `SafeEnumTypeHandler`를 작성해 `mybatis-config.xml`에 등록

---

## 6. PayoutDBO.findIdByPayout — 모델 ID 무시 + content matching

```java
// PayoutService.createPayout
String payoutId = generatePayoutId();
payoutDBO.save(payout, payoutId, policyNumber, "REGISTERED");
// payout.setPayoutId(payoutId) 호출되지 않음
```
이후 `payoutDBO.findIdByPayout(payout)`이 *processor/금액/공제항목 등 6개 필드 content matching*으로 ID 역추적. 동일 내용 데이터가 있으면 잘못된 ID를 반환할 위험.

### 권장
Service에서 `payout.setPayoutId(payoutId)`를 호출하고 `findIdByPayout` 메서드 자체를 제거.

---

## 7. ID 생성 동시성

모든 ID 생성이 `prefix + System.currentTimeMillis()` — 동일 모듈에서 1ms 내 두 건 발생 시 PK 충돌.

### 권장
- 공통 `IdGenerator` (UUID, ULID, 또는 sequence 기반) 도입
- 또는 PostgreSQL `SERIAL` / `IDENTITY` 컬럼으로 DB가 생성하게 하고 MyBatis `<selectKey>` / `useGeneratedKeys`로 받아오기

---

## 8. `DBA` 베이스 클래스의 dead methods

`DBA`는 더 이상 활성 DBO가 사용하지 않음. `connect/disconnect/login/executeSelect/executeInsert/executeUpdate/executeDelete/loadDbProperties` 메서드는 dead code.

### 권장
- stub DBO 17개를 정리하면서 `DBA`도 함께 정리 (자세한 내용은 `DEAD_DBO_NOTES.md`)
- 활성 DBO들은 `DBA` 상속을 끊거나, 공통 베이스가 필요하면 `db.mybatis.AbstractMyBatisDBO` 도입

---

## 9. 트랜잭션 경계

MyBatis SessionFactory는 `<transactionManager type="JDBC"/>`를 쓰고 있으며 `MyBatisSessionFactory.openSession(true)`로 *autocommit*을 켠 상태. 현재 콘솔 흐름은 단건 INSERT/UPDATE라 문제 없음.

### 권장
- 여러 테이블에 걸친 작업(예: AccidentReport + DamageInvestigation 동시 갱신)이 추가될 때 `openSession(false)` + `commit/rollback`을 명시적으로 사용
- 또는 Service 계층에 `@Transactional`(Spring) 도입 검토

---

## 10. 우선순위 요약

| 순위 | 항목 |
|---|---|
| 🔴 1 | §1 — `save(M)` UnsupportedOperationException 제거 (해결안 B: 모델 보강) |
| 🔴 1 | §2 — `AccidentReport.accidentStatus` 타입 의미 충돌 해결 |
| 🟠 2 | §3 — fetched 객체 부분 데이터 (§1과 함께) |
| 🟠 2 | §4 — DBO에서 도메인 변환 로직 분리 |
| 🟡 3 | §6 — Payout ID 역조회 제거 |
| 🟡 3 | §7 — 공통 IdGenerator |
| 🟢 4 | §5 — SafeEnumTypeHandler (필요 시) |
| 🟢 4 | §8, §9 — DBA cleanup, 트랜잭션 |
