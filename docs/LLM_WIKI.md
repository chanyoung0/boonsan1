# Boonsan1 LLM WIKI

## 0. Current Working Notes

현재 1차 배포 구조는 Nginx/VPS가 아니라 Cloudflare + Vercel + Render + Supabase 구조이다.

- Frontend: Vercel React/Vite
- Backend: Render Spring Boot
- DB: Supabase PostgreSQL
- DNS: Cloudflare
- Domain: mjusw.site

문서의 Nginx 구조는 향후 VPS 전환 시 고려하는 구조이다.

현재 Spring Boot 코드는 별도 backend 폴더가 아니라 루트 src 하위에 있다.
기존 패키지 구조를 임의로 바꾸지 않는다.

현재 보상 처리 완료 범위:
사고접수 → 손해조사 → 지급품의서 → 결재 승인/반려 → 보험금 지급 → 구상 처리 → 이의제기 처리

소송 처리는 법무팀 담당으로 보고 직접 구현하지 않는다.

---

## 1. Project Goal

Boonsan1은 보험사 업무 시스템을 기존 Java 콘솔 기반 구조에서 웹 기반 시스템으로 전환하는 프로젝트이다.

최종 목표는 React + Vite + TypeScript 프론트엔드와 Spring Boot REST API 백엔드, PostgreSQL DB를 사용하는 동적 웹 서비스 구현이다.

본 프로젝트의 최우선 기준은 다음 자료이다.

1. 신동아화재 차세대 시스템 구축 RFP
2. 분산프로그래밍1 중간 리포트
3. 유스케이스 다이어그램
4. 유스케이스 시나리오
5. 클래스 다이어그램
6. 현재 구현된 Java Service / DAO / Model 코드

불확실한 부분은 임의 판단하지 말고 사용자에게 질문한다.

---

## 2. Non-Negotiable Rules

- 기존 Service / DAO / Model 구조를 임의 삭제하지 않는다.
- 기존 기능을 웹 전환 과정에서 임의 제거하지 않는다.
- 기존 클래스명, 메서드명, 패키지명을 임의 변경하지 않는다.
- RFP / 유스케이스 / 시나리오 / 클래스 다이어그램을 최우선 기준으로 한다.
- Controller에 업무 로직을 작성하지 않는다.
- Service는 유스케이스 업무 로직을 담당한다.
- DAO / Repository는 DB 접근만 담당한다.
- Entity / Model을 React에 직접 JSON으로 반환하지 않는다.
- Request DTO / Response DTO를 분리한다.
- React Page에서 직접 fetch / axios를 남발하지 않는다.
- API 호출은 반드시 API Module로 분리한다.
- 외부 API는 Service에 직접 하드코딩하지 않고 External Client / Adapter 구조로 분리한다.
- 디자인은 사용자가 별도 제공하기 전까지 임의로 크게 변경하지 않는다.
- 작업 전에는 현재 구조를 분석하고, 수정 예정 파일을 먼저 보고한다.

---

## 3. Final Architecture

본 프로젝트는 다음 구조를 사용한다.

```text
Browser
 ↓
React Router
 ↓
React Page
 ↓
API Module
 ↓
Spring RestController
 ↓
Request DTO
 ↓
Service
 ↓
DAO / Repository
 ↓
PostgreSQL
 ↑
DAO / Repository
 ↑
Entity / Model
 ↑
Service
 ↑
Response DTO
 ↑
RestController
 ↓
JSON
 ↓
React State
 ↓
화면 출력
```

각 계층의 역할은 다음과 같다.

- Browser: 사용자가 보는 웹 브라우저
- React Router: `/products`, `/underwriting`, `/contracts`, `/claims` 등 화면 경로 분기
- React Page: 실제 화면 단위
- API Module: Spring REST API 호출 전담
- Spring RestController: HTTP 요청을 받는 백엔드 입구
- Request DTO: React에서 넘어온 입력값
- Service: 유스케이스 업무 로직 처리
- DAO / Repository: DB 접근
- PostgreSQL: 실제 데이터 저장소
- Entity / Model: DB 또는 도메인 내부 데이터 객체
- Response DTO: React로 반환할 응답 객체
- JSON: 프론트엔드와 백엔드의 통신 형식
- React State: API 응답을 화면 상태로 저장

---

## 4. Web Application Type

본 프로젝트는 정적 페이지가 아니라 동적 웹앱이다.

이유는 다음과 같다.

- 사용자 입력이 존재한다.
- 서버 처리가 필요하다.
- DB 조회와 저장이 필요하다.
- 업무 상태 변경이 필요하다.
- 외부 API 또는 Mock 외부 API 연동이 필요하다.
- 사용자와 업무 흐름에 따라 화면 결과가 달라진다.

React는 SPA 방식으로 동작한다.

최초 접속 흐름:

```text
React 앱 로드
→ React Router가 현재 URL 확인
→ 해당 Page Component 출력
```

기능 실행 흐름:

```text
React Page
→ API Module
→ Spring REST API 호출
→ DB 조회 / 저장
→ JSON 응답
→ React State 변경
→ 화면 일부 갱신
```

---

## 5. Web Server / WAS

최종 배포 기준 Web Server는 Nginx이다.

Nginx 역할:

- 사용자의 HTTP 요청을 가장 먼저 받는다.
- React + Vite build 정적 파일을 제공한다.
- `/api` 요청을 Spring Boot로 reverse proxy한다.
- React Router 경로를 처리하기 위해 프론트엔드 경로는 `index.html`로 전달한다.
- 운영 환경에서 HTTPS 설정을 담당한다.

WAS는 Spring Boot 내장 Tomcat이다.

Spring Boot WAS 역할:

- RestController 실행
- Service 실행
- DAO / Repository 실행
- DB 조회 / 저장
- 트랜잭션 처리
- 외부 API Client 호출
- JSON 응답 생성

최종 배포 구조:

```text
Browser
 ↓
Nginx
 ├─ /      → React build 정적 파일
 └─ /api   → Spring Boot REST API
                ↓
              Service
                ↓
              DAO / Repository
                ↓
              PostgreSQL
```

---

## 6. Monolith / MSA Decision

본 프로젝트는 MSA를 사용하지 않는다.

MSA를 사용하지 않는 이유:

- 기능별 서버 분리가 필요하다.
- 서비스 간 통신 설계가 필요하다.
- API Gateway가 필요하다.
- 인증 전달이 복잡해진다.
- 장애 처리와 로그 추적이 복잡해진다.
- 배포 자동화 부담이 커진다.
- 현재 프로젝트 범위에 비해 과하다.
- 보험 업무는 청약 심사, 증권 발행, 계약 생성, 계약 관리, 보상 처리 흐름이 서로 강하게 연결되어 있다.

따라서 본 프로젝트는 모듈형 모놀리식 구조를 사용한다.

원칙:

```text
서버는 하나
패키지는 업무 영역별로 분리
```

---

## 7. Backend Structure

백엔드는 Spring Boot REST API 기반 모듈형 모놀리식으로 구성한다.

권장 구조:

```text
backend/src/main/java/com/boonsan
 ├─ product
 ├─ underwriting
 ├─ contract
 ├─ claim
 ├─ external
 ├─ auth
 ├─ common
 ├─ config
 └─ exception
```

각 업무 모듈 내부 권장 구조:

```text
product
 ├─ controller
 ├─ service
 ├─ dao
 ├─ dto
 ├─ mapper
 └─ model
```

동일하게 `underwriting`, `contract`, `claim` 모듈도 구성한다.

각 계층 역할:

- controller: REST API 요청/응답 처리
- service: 유스케이스 업무 로직 처리
- dao / repository: DB 접근
- dto: Request DTO / Response DTO
- mapper: DTO와 Model 변환
- model: 도메인 객체 또는 Entity

기존 프로젝트가 이미 별도 패키지 구조를 가지고 있다면, 기존 구조를 먼저 분석하고 사용자 승인 없이 전면 변경하지 않는다.

---

## 8. Frontend Structure

프론트엔드는 React + Vite + TypeScript를 사용한다.

권장 구조:

```text
frontend/src
 ├─ api
 ├─ pages
 ├─ components
 ├─ types
 ├─ routes
 ├─ styles
 ├─ App.tsx
 └─ main.tsx
```

세부 구조:

```text
api
 ├─ apiClient.ts
 ├─ productApi.ts
 ├─ underwritingApi.ts
 ├─ contractApi.ts
 └─ claimApi.ts

pages
 ├─ product
 ├─ underwriting
 ├─ contract
 └─ claim

types
 ├─ product.ts
 ├─ underwriting.ts
 ├─ contract.ts
 └─ claim.ts
```

프론트엔드 원칙:

- Page에서 직접 fetch / axios를 호출하지 않는다.
- API 호출은 `api` 모듈에서 담당한다.
- 백엔드 Response DTO와 대응되는 TypeScript 타입을 `types`에 정의한다.
- 입력값 상태, 로딩 상태, 오류 상태, 결과 상태를 분리한다.
- 디자인은 사용자 제공 기준을 따른다.

---

## 9. API Design Rule

API는 유스케이스 기준으로 설계한다.

기본 영역:

```text
/product
/underwriting
/contract
/claim
```

최종 API prefix는 `/api`를 사용한다.

예시:

```text
GET    /api/products
POST   /api/products
POST   /api/products/{productCode}/authorization

GET    /api/underwriting/applications
POST   /api/underwriting/reviews
POST   /api/underwriting/credit-checks
POST   /api/underwriting/coinsurance
POST   /api/underwriting/reinsurance

GET    /api/contracts
GET    /api/contracts/{contractNumber}
POST   /api/contracts/{contractNumber}/endorsements
POST   /api/contracts/{contractNumber}/revivals
POST   /api/contracts/{contractNumber}/payment-collections

GET    /api/claims
POST   /api/claims/accident-reports
POST   /api/claims/{claimNumber}/investigations
POST   /api/claims/{claimNumber}/payments
POST   /api/claims/{claimNumber}/subrogations
```

API 명세는 실제 코드 분석 후 확정한다.

---

## 10. DTO Rule

DTO는 반드시 Request DTO와 Response DTO로 분리한다.

Request DTO:

- React에서 Spring으로 들어오는 입력값
- 입력 검증 대상

Response DTO:

- Spring에서 React로 반환하는 결과값
- 화면 표시용 데이터
- 민감 정보는 마스킹 후 반환

Entity / Model:

- DB 또는 도메인 내부에서 사용하는 객체
- React에 직접 노출하지 않는다

금지 사항:

- Entity를 Request DTO로 직접 사용 금지
- Entity를 Response DTO로 직접 반환 금지
- Controller에서 Entity를 그대로 JSON 반환 금지
- 민감 정보 마스킹 없이 반환 금지

---

## 11. External API Rule

외부 API는 Service에 직접 하드코딩하지 않는다.

외부 API는 `external` 패키지에 Client / Adapter 구조로 분리한다.

외부 시스템 후보:

- 금융감독원
- 한국신용정보원 ICIS
- 재보험사
- 공동인수사
- 은행 자동이체 시스템
- 협력업체 시스템

권장 구조:

```text
external
 ├─ fss
 ├─ icis
 ├─ reinsurance
 ├─ coinsurance
 └─ bank
```

원칙:

- 초기 구현은 Mock Client로 한다.
- 실제 API 연결 시 Service 수정 없이 Real Client로 교체 가능해야 한다.
- 외부 API 실패는 공통 예외로 처리한다.
- 외부 API 응답 DTO와 내부 Response DTO는 분리한다.

---

## 12. Error / Validation / Transaction Rule

### Error

예외는 Controller마다 try-catch 하지 않는다.

전역 예외 처리기를 사용한다.

권장 클래스:

```text
GlobalExceptionHandler
BusinessException
ErrorCode
ErrorResponse
```

### Validation

형식 검증은 Request DTO에서 처리한다.

예:

- 필수값
- 숫자 형식
- 날짜 형식
- 문자열 길이
- 금액 양수 여부

업무 검증은 Service에서 처리한다.

예:

- 계약이 유효한가
- 이미 만기인가
- 지급 가능한 상태인가
- 심사 대상인가
- 재보험 대상인가
- 공동인수 대상인가

### Transaction

DB 변경이 여러 개 묶이는 유스케이스는 Service에 트랜잭션을 적용한다.

Controller에 트랜잭션을 걸지 않는다.

DAO에 유스케이스 단위 트랜잭션을 맡기지 않는다.

---

## 13. State Rule

상태값은 문자열로 난립시키지 않는다.

업무 상태는 Enum으로 관리한다.

예:

```text
ApplicationStatus
ContractStatus
ClaimStatus
PaymentStatus
ApprovalStatus
```

원칙:

- 상태 변경은 Service에서만 수행한다.
- Controller에서 직접 상태를 변경하지 않는다.
- DAO는 상태 저장/조회만 담당한다.
- 화면 표시용 한글명은 DTO 또는 프론트에서 별도 처리한다.

---

## 14. Design Pattern Rule

반드시 적용할 구조:

- Layered Architecture
- Modular Monolith
- DTO Pattern
- DAO / Repository Pattern
- Adapter Pattern for External API
- Global Exception Handling

필요 시 적용할 패턴:

- Strategy Pattern: 심사 유형, 계산 방식이 복잡해질 때
- Factory Pattern: Strategy 또는 Client 선택이 필요할 때
- Facade Pattern: 하나의 유스케이스가 여러 DAO / 외부 API를 묶을 때
- State Pattern: 상태 전이 규칙이 복잡해질 때
- Template Method Pattern: 유사한 업무 흐름이 반복될 때

패턴은 억지로 넣지 않는다.
유스케이스 구현상 필요할 때만 적용한다.

---

## 15. Codex Working Rule

Codex는 작업 전 반드시 이 문서를 먼저 읽는다.

### Before Work

- 현재 프로젝트 구조를 분석한다.
- 기존 Service / DAO / Model 위치를 확인한다.
- console client 의존성을 확인한다.
- 변경 대상 파일을 먼저 보고한다.
- 불확실한 부분은 질문한다.

### During Work

- 기존 기능을 임의 삭제하지 않는다.
- 기존 클래스명과 메서드명을 임의 변경하지 않는다.
- Service / DAO / Model 구조를 임의 파괴하지 않는다.
- Controller, DTO, React Page는 유스케이스와 매핑되도록 작성한다.
- 외부 API는 실제 연동 전 Mock Client로 작성한다.

### After Work

- 변경 파일 목록을 제시한다.
- 어떤 유스케이스와 연결되는지 설명한다.
- 빌드 / 컴파일 결과를 확인한다.
- 오류가 있으면 원인과 수정 방향을 제시한다.

---

## 16. First Task Rule

처음부터 구현하지 않는다.

첫 작업은 분석이다.

Codex는 처음 작업에서 다음만 보고한다.

1. 현재 Spring 프로젝트 구조
2. 기존 service / dao / model 위치
3. console client 의존성 위치
4. React + Vite frontend를 추가할 적절한 위치
5. LLM_WIKI 기준과 충돌하는 부분
6. 첫 번째로 웹 전환하기 좋은 기능 후보
7. 수정이 필요할 것으로 예상되는 파일 목록

사용자 승인 전 코드 수정 금지.
