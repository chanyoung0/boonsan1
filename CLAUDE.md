# CLAUDE.md

## 프로젝트 목적
신동아화재 RFP, 유스케이스 다이어그램, 내부 시나리오, 클래스 다이어그램을 기반으로 Java TEXT-BASE 보험사 시스템을 구현한다.
1차 목표는 웹/DB/클라우드 없이 Java 콘솔에서 동작하는 프로그램이다.

## 기준 자료 우선순위
1. RFP 원본
2. 피드백 완료 유스케이스 다이어그램
3. 유스케이스 내부 시나리오
4. 클래스 다이어그램
5. 프로젝트 규칙 문서
6. Java 코드

## 개발 원칙
- 임의 기능 추가 금지
- 임의 흐름 변경 금지
- 애매한 부분은 팀원 또는 상급자에게 확인
- 외부 API, DB, 은행, 웹 연동은 현재 단계에서 구현하지 않음
- 외부 연동은 메시지 출력으로 대체
- 상태값은 가능하면 실제 객체에 반영

## 주석 규칙
- 클래스에는 역할을 설명하는 한 줄 주석을 작성한다
- 생성자에는 초기화 목적을 설명하는 한 줄 주석을 작성한다
- 핵심 기능 메서드에는 기능을 설명하는 한 줄 주석을 작성한다
- getter/setter와 toString에는 특별한 경우가 아니면 주석을 작성하지 않는다
- 필드는 의미가 불명확한 경우에만 주석을 작성한다

## 네이밍 규칙
- 패키지: 모두 소문자. 예) model.product, model.contract
- 클래스: PascalCase. 예) Insurance, Contract, AccidentReport
- 메서드: camelCase, 동사로 시작. 예) designInsurance(), requestAuthorization()
- 변수: camelCase. 예) contractNumber, insuredName
- 상수: UPPER_SNAKE_CASE. 예) MAX_RETRY_COUNT
- enum 클래스명: PascalCase. 예) ProductStatus
- enum 값: UPPER_SNAKE_CASE. 예) ProductStatus.DESIGNED
- Java 파일명: 클래스명과 동일. 예) Insurance.java

## 패키지 구조
```
src/
 ├─ Main.java          # 메인 실행 클래스 (루트)
 ├─ common/            # ConsoleUtil (입력/출력 유틸)
 ├─ enums/             # 모든 enum 클래스
 ├─ insurance/         # Insurance, AutoInsurance, FireInsurance, MarineInsurance,
 │                     # Authorization, FinancialSupervisoryService, UnderwritingService
 ├─ underwriting/      # InsuranceApplication, Underwriting, UnderwritingResult,
 │                     # UnderwritingHistory, UnderwritingRequest, Coinsurance, Coinsurer, Reinsurance
 ├─ contract/          # Contract 관련 모델 + EndorsementService, ReinstatementService,
 │                     # PaymentCollectionService, MaturityContractService
 ├─ accident/          # AccidentReport, DamageInvestigation, InsurancePayment,
 │                     # Objection, OutsourceRequest, Subrogation, AccidentHistory,
 │                     # AccidentReportService, DamageInvestigationService
 ├─ document/          # Document, AccidentDocument, PaymentApprovalDocument
 ├─ person/            # InsuredPerson, Account, Manager
 └─ partner/           # Partner
```

## 유스케이스 → 코드 변환 규칙
유스케이스 하나는 Service의 run() 또는 메서드 하나로 구현한다.

- 보험청약을 심사한다 → UnderwritingService.run() (insurance 패키지)
- 신용정보를 조회한다 → UnderwritingService 내부 메서드 (include)
- 청약서 및 증권발행을 한다 → UnderwritingService 내부 메서드 (include)
- 공동인수를 처리한다 → UnderwritingService 내부 메서드 (extend)
- 재보험 처리를 한다 → UnderwritingService 내부 메서드 (extend)
- 배서를 관리한다 → EndorsementService.run() (contract 패키지)
- 부활을 관리한다 → ReinstatementService.run() (contract 패키지)
- 분납/수금을 관리한다 → PaymentCollectionService.run() (contract 패키지)
- 만기계약을 관리한다 → MaturityContractService.run() (contract 패키지)
- 사고를 접수한다 → AccidentReportService.run() (accident 패키지)
- 손해조사를 한다 → DamageInvestigationService.run() (accident 패키지)

- Basic Path: 기본 실행 흐름으로 구현한다
- Alternate Flow: 사용자 입력 또는 조건문으로 분기한다
- Exception Flow: 오류 발생 여부를 입력받거나 실패 메시지를 출력한다
- Include 관계: 포함되는 유스케이스 메서드를 내부에서 호출한다. 예) reviewApplication() 내부에서 searchCreditInformation() 호출
- Extend 관계: 조건이 만족될 때만 확장 유스케이스 메서드를 호출한다. 예) 공동인수가 필요한 경우 processCoInsurance() 호출

## 콘솔 출력 규칙
- 모든 기능은 Main 메뉴에서 시작한다
- 사용자는 숫자 메뉴를 입력한다
- 입력은 Scanner 또는 InputUtil을 사용한다
- 출력은 System.out.println()을 사용한다

유스케이스 시작 출력 형식:
[유스케이스] 유스케이스명
액터: 액터명
예) [유스케이스] 보험청약을 심사한다
    액터: 언더라이터

시스템 메시지 형식:
[시스템] 메시지 내용
예) [시스템] 계약정보 조회 중...
    [시스템] DB에 저장 중...
    [시스템] 상태: '심사 완료'

액터 행동 형식:
[액터명] 행동 내용
예) [언더라이터] '심사점수 계산 및 보고서 출력' 버튼을 누릅니다.
    [보험가입자] '사고 접수' 버튼을 누릅니다.

오류 메시지 형식:
[오류] 오류 내용
예) [오류] 저장 실패.
    [오류] ICIS API가 응답하지 않습니다.

입력 프롬프트 형식:
>> 선택: (메뉴 선택)
  필드명: (데이터 입력)

include/extend 호출 형식:
>> <<include>> [유스케이스명] 시나리오 시작
>> <<extend>> [유스케이스명] 시나리오 자동 시작

## 상태값 Enum (실제 코드 기준)

모든 enum은 `enums` 패키지에 위치한다.

실제 구현된 주요 enum:
- ApplicationStatus: PENDING, APPROVED, REJECTED, CANCELLED
- AccidentType: VEHICLE_ACCIDENT, PROPERTY_DAMAGE, INJURY, FIRE, NATURAL_DISASTER
- CompensationStatus: IN_PROGRESS, COMPLETED, CLOSED
- UnderwritingResultType, UnderwritingType, RejectionReason, RequestStatus 등

※ 실제 enum 값은 src/enums/ 디렉토리의 파일을 직접 확인할 것

## 메뉴 구조 (실제 Main.java 기준)

메인 메뉴 (flat 구조):
1. 보험청약 심사
2. 배서 관리
3. 부활 관리
4. 분납/수금 관리
5. 만기계약 관리
6. 사고 접수
7. 손해조사
8. 종료

## 클래스 매핑 (한글 → Java)

상품 개발:
- 보험 → Insurance
- 자동차보험 → AutoInsurance
- 화재보험 → FireInsurance
- 해상보험 → MarineInsurance
- 인가 → Authorization
- 금융감독원 → FinancialSupervisoryService

U/W:
- 청약 → InsuranceApplication
- 심사 → Underwriting
- 심사결과 → UnderwritingResult
- 심사이력 → UnderwritingHistory
- 사고이력 → AccidentHistory
- 공동인수 → Coinsurance
- 공동인수사 → Coinsurer
- 재보험 → Reinsurance

계약 관리:
- 계약 → Contract
- 배서 → Endorsement
- 부활 → Reinstatement
- 심사요청 → UnderwritingRequest
- 분납/수금 → PaymentCollection
- 미납안내 → UnpaidNotice
- 이관 → Transfer
- 만기안내 → MaturityNotice
- 제지급금 → Payout
- 계좌 → Account
- 피보험자 → InsuredPerson

보상 처리:
- 사고접수 → AccidentReport
- 손해조사 → DamageInvestigation
- 위탁요청 → OutsourceRequest
- 협력업체 → Partner
- 보험금지급 → InsurancePayment
- 이의제기 → Objection
- 구상처리 → Subrogation
- 문서 → Document
- 사고접수서류 → AccidentDocument
- 지급품의서 → PaymentApprovalDocument

보상 기획:
- 보상평가 → CompensationEvaluation
- 협력업체 → Partner

## 구현 순서

1단계 - Java 기본 구조:
1. 패키지 생성
2. enum 생성
3. model 클래스 뼈대 생성
4. repository 생성
5. external mock 클래스 생성
6. util 클래스 생성
7. service 클래스 틀 생성
8. Main 메뉴 생성

2단계 - 내부 시나리오 적용:
1. 상품을 설계한다
2. 상품 인가를 요청한다
3. 보험청약을 심사한다
4. 신용정보를 조회한다
5. 청약서 및 증권발행을 한다
6. 공동인수를 처리한다
7. 재보험 처리를 한다
8. 배서를 관리한다
9. 부활을 관리한다
10. 심사를 요청한다
11. 분납/수금을 관리한다
12. 제지급금을 관리한다
13. 만기계약을 관리한다
14. 사고를 접수한다
15. 손해조사를 한다
16. 손해조사를 위탁한다
17. 보험금을 지급한다
18. 구상을 처리한다
19. 이의 제기를 처리한다
20. 보상 평가를 관리한다
21. 협력업체를 관리한다

## TODO

진행 전 확인:
- [ ] RFP 원본 확인
- [ ] 유스케이스 다이어그램 확인
- [ ] 내부 시나리오 확인
- [ ] 클래스 다이어그램 확인

Java 기본 구조:
- [ ] 패키지 생성
- [ ] enum 생성
- [ ] model 클래스 생성
- [ ] MemoryRepository 생성
- [ ] external mock 클래스 생성
- [ ] util 클래스 생성
- [ ] service 클래스 생성
- [ ] Main 메뉴 생성

내부 시나리오 적용:
- [ ] 상품을 설계한다
- [ ] 상품 인가를 요청한다
- [ ] 보험청약을 심사한다
- [ ] 신용정보를 조회한다
- [ ] 청약서 및 증권발행을 한다
- [ ] 공동인수를 처리한다
- [ ] 재보험 처리를 한다
- [ ] 배서를 관리한다
- [ ] 부활을 관리한다
- [ ] 심사를 요청한다
- [ ] 제지급금을 관리한다
- [ ] 분납/수금을 관리한다
- [ ] 만기계약을 관리한다
- [ ] 사고를 접수한다
- [ ] 손해조사를 한다
- [ ] 손해조사를 위탁한다
- [ ] 보험금을 지급한다
- [ ] 구상을 처리한다
- [ ] 이의 제기를 처리한다
- [ ] 보상 평가를 관리한다
- [ ] 협력업체를 관리한다