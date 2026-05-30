# 클래스 다이어그램과 코드의 매핑

## 배경

설계 단계의 클래스 다이어그램에는 도메인 클래스마다 메서드가 그려져 있다. 하지만 이 프로젝트는 그 로직을 도메인이 아니라 Service로 옮긴 구조다. 즉 다이어그램의 메서드는 "이 도메인이 관여하는 동작"을 표현한 것이고, 실제 구현은 Service에 있다.

도메인 메서드와 Service 구현의 대응 관계는 세 가지다. 새 코드를 짤 때 이 패턴을 따른다.

(참고: 클래스 단위로도 도메인 37개 : Service 12개로 N:1이다. Service는 유스케이스 단위로 묶이기 때문이며, 이 상시 규칙은 CLAUDE.md에 있다. 따라서 메서드도 여러 도메인의 것이 한 Service에 모일 수 있다.)

## 1:1 — 이름만 풀어 쓴 경우 (다이어그램 이름 기준 유지)
다이어그램의 동사형 메서드를 Service에서 "동사+명사"로 풀어 쓴 것.

예:
- `AccidentReport.register()` → `AccidentReportService.registerAccidentReport()`
- `Partner.update()` → `PartnerService.updatePartner()`

→ 동작이 1:1로 대응되므로 다이어그램 이름을 기준으로 두고 유지한다.

## 1:N — 하나가 여러 단계로 쪼개진 경우 (합치지 마라)
다이어그램의 메서드 하나가 구현 시 여러 단계로 나뉜 것.

예:
- `Underwriting.calculateScore()` → `UnderwritingService`의 `calculateInputScore()` + `getManualUnderwritingAdjustment()` + `determineResult()` + `canAutoReview()`

→ 억지로 하나로 합치지 마라. 합치면 단일 책임이 깨지고, 유스케이스 시나리오의 단계 분리(점수 산출 → 판정 입력)를 표현할 수 없다. 구현이 설계보다 정밀해진 정상적인 경우다.

## N:1 — 여러 개가 하나로 흡수된 경우 (떼어내지 마라)
다이어그램의 별도 메서드가 다른 메서드 안에 흡수된 것.

예:
- `Subrogation.generateSubrogationDocument()` → `SubrogationService.createSubrogation()` 안에 포함

→ 억지로 떼어내지 마라. 떼면 빈 껍데기 메서드로 되돌아간다.

## 원칙
**1:1만 다이어그램 이름을 기준으로 맞추고, 1:N·N:1은 현재 구현을 유지한다.**

---

## 도메인의 빈 껍데기 메서드 처리 기준

도메인 클래스에는 다이어그램에서 온 빈 메서드(`{}`)가 많이 남아 있다. 처리 기준은 다음과 같다.

- **자기 필드만 바꾸는 단순 동작**은 도메인에 남기고 구현해도 된다 (의존성 원칙 위반 아님).
  예: `Payout.approvePayment()`가 자신의 `approvedAt`을 세팅, `Contract.checkPaymentStatus()`가 자신의 필드 반환, `Account.checkBalance()`가 자신의 `balance`를 보고 boolean 반환.
- **DB 저장·외부 전송·심사 실행 등 외부 협력이 필요한 동작**은 도메인에 두지 말고 Service의 책임으로 옮긴다.
- 빈 메서드를 호출만 하고 본문이 비어 있으면 "동작하는 것처럼 보이는데 아무 일도 안 하는" 거짓 신호가 되니 주의한다.

빈 메서드를 채울지 비울지의 구체적 판단은 `unimplemented-policy.md`를 참고한다.
