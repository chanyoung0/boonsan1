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
 ├─ app/              # Main 실행 클래스
 ├─ model/
 │   ├─ common/       # enum 등 공통 상태값
 │   ├─ product/
 │   ├─ underwriting/
 │   ├─ contract/
 │   └─ claim/
 ├─ service/          # 유스케이스 내부 시나리오 흐름 구현
 ├─ repository/       # DB 대신 사용하는 메모리 저장소
 ├─ external/         # 외부 API 연동 예정 Mock 클래스
 └─ util/             # 입력 처리, ID 생성 등 공통 유틸
```

## 유스케이스 → 코드 변환 규칙
유스케이스 하나는 Service 메서드 하나로 구현한다.

- 상품을 설계한다 → ProductService.designInsurance()
- 상품 인가를 요청한다 → ProductService.requestAuthorization()
- 보험청약을 심사한다 → UnderwritingService.reviewApplication()
- 신용정보를 조회한다 → UnderwritingService.searchCreditInformation()
- 사고를 접수한다 → ClaimService.registerAccident()
- 보험금을 지급한다 → ClaimService.payInsuranceMoney()

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

기능 실행 출력 형식:
[유스케이스명] 기능을 실행합니다.
예) [보험청약을 심사한다] 기능을 실행합니다.

외부 연동 출력 형식:
[외부 연동 예정] OO API를 연동시킬 예정입니다.
예) [외부 연동 예정] 한국신용정보원(ICIS) API를 연동시킬 예정입니다.

DB 저장 출력 형식:
[DB 연동 예정] OO 정보를 DB에 저장할 예정입니다.
[메모리 저장 완료] OO 정보가 프로그램 실행 중 임시 저장되었습니다.

상태 변경 출력 형식:
[상태 변경] 청약 상태: REVIEW_COMPLETED

## 상태값 Enum

ProductStatus
- PLANNED: 상품 기획 완료
- DESIGNED: 상품 설계 완료
- AUTHORIZATION_REQUESTED: 인가 요청
- AUTHORIZED: 인가 완료
- REJECTED: 인가 불허
- REVISION_REQUESTED: 보완 요청

ContractStatus
- ACTIVE: 유효
- LAPSED: 실효
- TERMINATED: 해지
- MATURED: 만기
- UNPAID: 미납
- REINSURANCE_COMPLETED: 재보험 처리 완료

ApplicationStatus
- RECEIVED: 청약 접수
- UNDER_REVIEW: 심사 중
- REVIEW_COMPLETED: 심사 완료
- POLICY_ISSUED: 증권 발행 완료

UnderwritingDecision
- APPROVED: 승인
- EXTRA_PREMIUM: 할증
- REJECTED: 거절

AccidentStatus
- REGISTERED: 사고 접수
- FIELD_INVESTIGATION_REQUIRED: 현장 조사 필요
- APPROVAL_REQUIRED: 결재 필요
- PAYMENT_COMPLETED: 보험금 지급 완료
- SUBROGATION_REQUIRED: 구상 처리 필요
- CLOSED: 종결
- REJECTED: 반려
- FRAUD_INVESTIGATION: 보험사기 조사
- TEMP_SAVED: 임시 저장

## 메뉴 구조

메인 메뉴:
1. 상품 개발
2. U/W 언더라이팅
3. 계약 관리
4. 보상 처리
5. 보상 기획
0. 종료

상품 개발 메뉴:
1. 상품을 설계한다
2. 상품 인가를 요청한다
3. 상품 목록 조회
0. 뒤로가기

U/W 메뉴:
1. 보험청약을 심사한다
2. 신용정보를 조회한다
3. 청약서 및 증권발행을 한다
4. 공동인수를 처리한다
5. 재보험 처리를 한다
0. 뒤로가기

계약 관리 메뉴:
1. 배서를 관리한다
2. 부활을 관리한다
3. 심사를 요청한다
4. 제지급금을 관리한다
5. 분납/수금을 관리한다
6. 만기계약을 관리한다
0. 뒤로가기

보상 처리 메뉴:
1. 사고를 접수한다
2. 손해조사를 한다
3. 손해조사를 위탁한다
4. 보험금을 지급한다
5. 구상을 처리한다
6. 이의 제기를 처리한다
0. 뒤로가기

보상 기획 메뉴:
1. 보상 평가를 관리한다
2. 협력업체를 관리한다
0. 뒤로가기

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
- 공동인수 → CoInsurance
- 공동인수사 → CoInsurer
- 재보험 → ReInsurance

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