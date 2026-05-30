# 구상 처리(Subrogation) 메인 메뉴 추가 작업

구상 처리는 유스케이스 12번이지만, 현재 Main 메뉴에서 실행할 수 없는 상태다.
이 문서는 구상 처리를 메인에 올리기 위해 해야 할 일만 정리한다.
(이것은 unimplemented-policy.md의 5번 분리 작업과 별개다. 구상은 도메인·Service가 이미 있고
Console·Mapper·테이블·Main연결이 빠진 경우라 작업 내용이 다르다.)

## 현재 상태 (정확히)

| 구성 요소 | 상태 |
|----------|------|
| 도메인 `model/accident/Subrogation.java` | 있음 |
| `service/accident/SubrogationService.java` | 있음 (단 DB 저장 로직 없음 — 객체 생성·계산만 함) |
| `console/.../SubrogationConsole.java` | **없음** (콘솔 미작성) |
| Mapper (`db/SubrogationMapper.java` + XML) | **없음** |
| 테이블 (`schema.sql`의 subrogation) | **없음** |
| `Main.java`의 메뉴 연결 | **없음** (현재 메뉴는 1~11번까지만, 구상 없음) |

## 이미 있는 것 (새로 만들지 말 것)

### 도메인 Subrogation 필드
`subrogationId`, `subrogationStatus`, `offenderName`, `offenderContact`, `faultRatio`, `paymentAmount`, `depositAccount`, `paymentDeadline`

### SubrogationService 기존 메서드 (그대로 활용)
- `createSubrogation(InsurancePayment payment, String offenderName, ...)` — 구상 객체 생성
- `sendClaim(Subrogation)` — 구상 청구서 발송 (상태 변경)
- `confirmDeposit(Subrogation, String answer)` — 입금 확인 (상태 변경)
- `calculateSubrogationAmount(BigDecimal paidAmount, float faultRatio)` — 구상 금액 계산 (지급액 × 과실비율)
- `parseFaultRatio(String)`, `resolvePaymentDeadline(String)` — 입력 파싱 헬퍼

## 해야 할 일 (4단계)

### 1단계. 테이블 추가 (`schema.sql`)
subrogation 테이블을 만든다. 컬럼은 도메인 필드에 맞춘다.
- subrogation_id (PK), subrogation_status, offender_name, offender_contact, fault_ratio, payment_amount, deposit_account, payment_deadline
- PostgreSQL 문법으로 작성한다.

### 2단계. Mapper 생성
- `db/SubrogationMapper.java` (인터페이스) — `insert`, `findById`, `findAll`, `findStatusById` 등 선언. SQL 애노테이션 쓰지 말 것.
- `src/main/resources/db/mapper/SubrogationMapper.xml` — 위 메서드의 SQL과 resultMap. resultMap은 DB 컬럼 → Subrogation 필드 매핑. `subrogationStatus`는 enum이므로 `javaType="enums.SubrogationStatus"`.
- `mybatis-config.xml`의 `<mappers>`에 새 XML 등록.

### 3단계. SubrogationService에 DB 저장 로직 추가
기존 메서드는 두고, 저장·조회 메서드를 추가한다. 다른 Service(예: PayoutService)의 패턴을 그대로 따른다.
```java
public static String saveSubrogation(Subrogation subrogation) {
    try (SqlSession s = MyBatisSessionFactory.openSession()) {
        int rows = s.getMapper(SubrogationMapper.class).insert(subrogation);
        return rows > 0 ? subrogation.getSubrogationId() : null;
    } catch (Exception e) {
        System.out.println("[DB 오류] 구상 저장 실패: " + e.getMessage());
        return null;
    }
}
// findAll, findById 등도 동일 패턴으로 추가
```

### 4단계. Console 생성 + Main 연결
- `console/accident/SubrogationConsole.java`를 새로 만든다. 다른 콘솔(예: PayoutConsole)의 구조를 따른다: 입력 받기 → SubrogationService 호출 → 결과 출력. `run()` static 메서드를 둔다.
  - 흐름(유스케이스 시나리오 "구상을 처리한다" 기준): 지급완료 사건 선택 → 사고정보·과실비율 입력 → 구상금액 계산 출력 → 구상청구서 발송(sendClaim) → 입금 확인(confirmDeposit) → 저장(saveSubrogation)
  - 주의: Console에서 `SqlSession`·Mapper 직접 사용 금지. 반드시 SubrogationService를 통한다.
- `Main.java`에 연결한다.
  - 상단에 `import console.accident.SubrogationConsole;` 추가
  - switch에 `case "12": SubrogationConsole.run(); break;` 추가
  - 메뉴 출력 문자열에도 "12. 구상 처리" 항목 추가

## 주의
- 도메인 Subrogation과 SubrogationService 기존 메서드는 새로 만들지 말고 활용한다.
- 단계 순서(테이블 → Mapper → Service 저장로직 → Console/Main)대로 만들고 각 단계마다 동작을 확인한다.
- 구상은 InsurancePayment(보험금지급)를 입력으로 받는다(`createSubrogation`). InsurancePayment가 어떻게 전달되는지 확인하고 연결한다.
