# 프로젝트 아키텍처 규칙

이 프로젝트는 Java + MyBatis 기반 보험 관리 시스템이다. DB는 PostgreSQL을 사용한다.
아래 규칙은 이 프로젝트의 코드를 다룰 때 항상 지킨다.

(클래스 다이어그램의 메서드를 구현/이동할지 판단하거나, 도메인의 빈 메서드를 채울지 비울지 판단할 때는
`insurance-system-architecture` 스킬을 참고한다.)

---

## 레이어 구조

```
Console → Service → Mapper 인터페이스 → Mapper XML → DB (PostgreSQL)
```

각 레이어는 딱 한 가지 책임만 가진다. 역할을 섞지 마라.

---

## 의존성 방향 원칙

의존성은 항상 아래 방향으로만 흐른다.

```
Console → Service → Mapper
               ↓
          도메인 객체 (model)
```

- 도메인 객체는 Service, Mapper, Console을 절대 `import`하거나 호출하지 않는다.
- Service는 Console을 모른다.
- 상위 레이어가 하위 레이어를 호출하는 것이지, 반대 방향은 없다.

---

## 데이터 흐름 방향

- **저장**: Service가 도메인 객체를 직접 생성하고 값을 채워서 Mapper에 넘긴다.
  `Service → (객체 생성) → Mapper → DB`
- **조회**: Mapper가 DB 결과를 `resultMap` 기준으로 도메인 객체에 담아 Service에 반환한다. Service는 받기만 한다.
  `DB → Mapper → (객체 반환) → Service`

객체를 만드는 주체가 방향에 따라 다르다 (저장은 Service, 조회는 MyBatis).

---

## 각 레이어의 책임

### Console (`console/`)
- 사용자 입출력 전담. `System.out`, `Scanner`는 여기서만.
- 비즈니스 로직 작성 금지. Service 메서드 호출만 한다.
- `SqlSession`, Mapper를 직접 쓰지 마라.

### Service (`service/`)
- 비즈니스 로직 전담 (점수 계산, 상태 판정, 유효성 검사 등).
- DB 작업은 `MyBatisSessionFactory.openSession()`으로 세션을 열고 Mapper를 호출한다.
- 세션은 반드시 try-with-resources로 연다.
  ```java
  try (SqlSession s = MyBatisSessionFactory.openSession()) {
      return s.getMapper(XxxMapper.class).findById(id);
  }
  ```
- Console에서 받은 값을 그대로 DB에 넣지 말고, 도메인 객체에 담아 넘긴다.

### 도메인 모델 (`model/`)
- 데이터를 담는 Java Bean. 필드 + getter/setter + 생성자가 기본이다.
- 비즈니스 로직을 도메인에 작성하지 마라. (자기 필드만 바꾸는 단순 동작은 예외)
- 필드명은 Mapper XML의 `resultMap`과 맞춰야 한다. 임의로 바꾸면 매핑이 깨진다.
- 패키지: `model.accident`, `model.contract`, `model.underwriting`, `model.insurance`, `model.partner`, `model.person`, `model.document`

### Mapper 인터페이스 (`db/`)
- 메서드 선언만. 구현체는 작성하지 않는다.
- SQL을 애노테이션(`@Select` 등)으로 쓰지 마라. SQL은 XML에만 작성한다.
- 파라미터가 2개 이상이면 `@Param`을 붙인다.

### Mapper XML (`src/main/resources/db/mapper/`)
- 실제 SQL과 `resultMap`이 여기에 있다.
- 새 XML을 추가하면 `mybatis-config.xml`의 `<mappers>`에도 등록한다.
- `mapUnderscoreToCamelCase`는 `false` 고정. 컬럼 매핑은 `resultMap`에 명시한다.

### enums (`enums/`)
- 상태값은 String 대신 enum을 쓴다. DB 저장 시 `.name()`으로 변환하고 XML에서 `javaType`으로 매핑한다.

---

## 데이터베이스

- DBMS는 **PostgreSQL** (`org.postgresql.Driver`). 다른 DB로 바꾸지 마라. SQL은 PostgreSQL 문법으로 작성한다.
- 스키마는 루트의 `schema.sql`에 있다. 테이블/컬럼 추가 시 함께 갱신한다.
- 접속 정보는 `db.properties`에서 읽는다 (`db.properties.example` 참고, 커밋 금지).
- `MyBatisSessionFactory`는 싱글톤이다. 수정하거나 우회해서 JDBC를 직접 쓰지 마라.

---

## 새 기능 추가 시 체크리스트

1. `model/` — 도메인 클래스/필드 추가
2. `db/` — Mapper 인터페이스에 메서드 선언
3. `src/main/resources/db/mapper/` — XML에 SQL과 resultMap 추가
4. `mybatis-config.xml` — 새 XML 등록 (신규 Mapper일 때만)
5. `service/` — 비즈니스 로직과 Mapper 호출 작성
6. `console/` — 입출력 및 Service 호출 작성

---

## 절대 하지 말아야 할 것

- Console에서 `SqlSession`/Mapper를 직접 사용
- Service에서 `System.out`/`Scanner` 사용
- Mapper 인터페이스에 SQL 애노테이션 작성
- 도메인 필드명을 `resultMap`과 안 맞추고 변경
- `MyBatisSessionFactory` 우회 후 JDBC 직접 사용
- 도메인이 Service/Mapper/Console을 호출 (의존성 방향 위반)
- PostgreSQL이 아닌 다른 DB 문법으로 SQL 작성
- 외부 API 연동(금융감독원/재보험사/공동인수사/ICIS) 메서드를 새로 구현 — 범위 밖이라 빈 채로 둔다
