---
name: insurance-system-architecture
description: "신동아화재 Java + MyBatis 보험 시스템에서 클래스 다이어그램의 메서드를 코드에 반영하거나, 도메인의 빈(미구현) 메서드를 채울지 비울지 판단할 때 사용하라. 다이어그램 메서드명과 Service 구현이 1:1/1:N/N:1로 어긋날 때, 보험료·재보험료·연체료 같은 계산 메서드를 구현할 때, 외부 연동(금융감독원/재보험사/공동인수사/ICIS) 메서드를 처리할 때, 구상 처리(Subrogation)를 메인 메뉴에 추가할 때 특히 중요하다. 도메인 메서드(calculatePremium, executeContract, register, sendNotice 등)를 손대기 전에 반드시 이 스킬로 판단 기준을 확인하라. 레이어 구조·의존성 같은 상시 규칙은 CLAUDE.md에 있다."
---

# 클래스 다이어그램 ↔ 코드 매핑과 미구현 메서드 판단

이 스킬은 "다이어그램에 그려진 도메인 메서드를 코드에서 어떻게 다룰지"만 다룬다.
레이어 구조, 의존성 방향, DB 규칙 같은 상시 규칙은 CLAUDE.md를 따른다.

## 언제 무엇을 보나

- **다이어그램의 메서드와 Service 구현 이름/개수가 다를 때**, 또는 그 메서드를 옮기거나 합칠지 고민될 때
  → `references/diagram-mapping.md`
- **도메인의 빈 메서드(`{}`)를 발견하고 채울지 비울지 판단할 때**
  → `references/unimplemented-policy.md`
- **구상 처리(Subrogation)를 메인 메뉴에 추가하거나 구상 관련 작업을 할 때**
  → `references/subrogation-main-integration.md` (구상은 도메인·Service는 있으나 Console·Mapper·테이블·Main연결이 빠진 특수 케이스)

## 한 줄 판단 가이드

자세한 근거가 필요하면 위 문서를 열되, 빠르게 판단할 때는 아래를 따른다.

- 다이어그램 메서드가 Service에 **이름만 바꿔** 1:1로 있다 → 다이어그램 이름 기준 유지
- 다이어그램 메서드 하나가 Service에서 **여러 개로** 쪼개졌다 → 합치지 마라
- 다이어그램 메서드 여러 개가 Service 한 곳에 **흡수**됐다 → 떼어내지 마라
- 빈 메서드가 `send`/`receive` 등 **외부 연동**이다 → 영구히 비운다 (구현 금지)
- 빈 메서드가 **계산**이다 → 공식대로 구현한다 (시나리오 공식 우선, 없으면 기본 비례식)
- 빈 메서드가 **회계 처리**다 → 비운다 (회계 모듈 없음)
- 빈 메서드가 **계약 실행/갱신/종료, 파일 업로드** 등 콘솔에 무대가 없는 것이다 → 비운다 (웹 단계 구현)
- 빈 메서드가 InsuredPerson/Account/Document/UnderwritingHistory 등 **Mapper 없는 도메인의 저장 동작**이다 → 다이어그램대로 테이블·Mapper·Service를 분리해 구현한다 (순서·관계는 unimplemented-policy.md 5번)
- 빈 메서드가 **자기 필드만 바꾸는** 단순 동작이다 → 도메인에 두고 구현해도 된다
