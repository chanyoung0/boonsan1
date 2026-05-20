# CLAUDE.md — Core Principles for Claude Code

> This file must be read and followed at all times.
> Even as sessions grow long and context is compacted, these principles are never forgotten.

---

## 0. Before Starting Any Task

Before writing a single line of code, always do the following:

1. Understand the existing code structure (packages, class names, naming conventions)
2. Check for similar classes or methods that already exist
3. Identify the patterns the team is using and follow them exactly
4. If uncertain about anything, ask first — then proceed

Never adopt a "just build it and see" approach.

---

## 1. My Known Weaknesses — Always Guard Against These

Claude Code repeatedly makes the following mistakes.
Acknowledge them and actively counteract them.

### 1-1. Context Amnesia
- In long sessions, early instructions get forgotten
- **Counter:** Re-read this file before starting each task to re-anchor principles

### 1-2. Style Inconsistency
- Naming conventions, formatting, and structure drift between sessions
- **Counter:** Always read existing code style first and match it 100%

### 1-3. Local Fix Trap
- Fixing only the requested part without understanding the full flow
- **Counter:** Before any change, trace the full call chain and check all affected classes

### 1-4. Over-generation
- Creating unrequested methods, classes, or excessive comments
- **Counter:** Build only what was asked. If something extra seems needed, ask first

### 1-5. Confident Incorrectness
- Presenting wrong code with full confidence
- **Counter:** Always flag uncertain parts explicitly. Never hide "I'm not sure"

### 1-6. Ignoring Existing Structure
- Introducing new patterns that override team-agreed architecture
- **Counter:** Never introduce new patterns unilaterally. Always confirm before changing structure

---

## 2. Object-Oriented Principles — No Exceptions

### 2-1. Single Responsibility Principle (SRP)
- One class = one responsibility
- If a method is getting long, it's a signal that responsibilities are mixing → consider splitting
- Ask yourself: "Can I describe what this class does in one sentence?"

```java
// Bad — one class doing too much
class PartnerService {
    public void savePartner() { ... }
    public void printReport() { ... }   // printing belongs in Console
    public void connectDB() { ... }     // DB connection belongs in DBO/DBA
}

// Good
class PartnerService {
    public void savePartner() { ... }   // business logic only
}
```

### 2-2. Encapsulation
- Fields are always `private`
- Only expose what needs to be public
- Do not generate getters/setters out of habit — only create what is actually needed

```java
// Bad
public String name;

// Good
private String name;
public String getName() { return name; }
```

### 2-3. Dependency Injection
- Never instantiate dependencies inside a class using `new`
- Always receive dependencies via constructor or method parameters

```java
// Bad
class PartnerService {
    private PartnerDBO dbo = new PartnerDBO(); // tight coupling
}

// Good
class PartnerService {
    private final PartnerDBO dbo;
    public PartnerService(PartnerDBO dbo) {
        this.dbo = dbo;
    }
}
```

### 2-4. Composition Over Inheritance
- Do not default to inheritance for shared behavior
- Prefer interfaces and composition first

### 2-5. Consistent Abstraction Level
- A single method must not mix high-level logic with low-level implementation

```java
// Bad — high-level and low-level mixed
public void processPartner(Partner p) {
    if (p.getName() == null || p.getName().isEmpty()) { // low-level
        throw new IllegalArgumentException();
    }
    partnerDBO.save(p); // high-level
}

// Good
public void processPartner(Partner p) {
    validate(p);        // high-level
    partnerDBO.save(p); // high-level
}

private void validate(Partner p) { // low-level detail isolated here
    if (p.getName() == null || p.getName().isEmpty()) {
        throw new IllegalArgumentException("Partner name must not be empty");
    }
}
```

---

## 3. Code Quality Standards

### 3-1. Naming
- No abbreviations: `mgr`, `tmp`, `val` → `manager`, `temp`, `value`
- Methods must start with a verb: `getPartner()`, `saveEvaluation()`, `calculateAmount()`
- Booleans use `is`, `has`, `can`: `isValid()`, `hasData()`, `canProcess()`
- Collections use plural form: `partnerList`, `evaluations`
- Always mirror the naming style of existing code in the project

### 3-2. Method Length
- If a method exceeds ~20 lines, consider splitting it
- Ideal: fits on screen without scrolling

### 3-3. Comments
- Do not comment what the code already says clearly
- Comments explain **why**, not **what**

```java
// Bad — the code already says this
// increment i by 1
i++;

// Good — explains the reason
// PostgreSQL JDBC defaults autoCommit to true,
// so we explicitly set it to false for transaction control
conn.setAutoCommit(false);
```

### 3-4. Exception Handling
- Empty catch blocks are strictly forbidden
- Every exception must be handled or rethrown with context
- Always include meaningful messages in exceptions

```java
// Bad
try {
    dbo.save(partner);
} catch (Exception e) { }

// Good
try {
    dbo.save(partner);
} catch (SQLException e) {
    throw new RuntimeException("Failed to save partner: " + partner.getId(), e);
}
```

### 3-5. No Magic Numbers or Strings
- Give all meaningful literals a named constant

```java
// Bad
if (grade > 80) { ... }

// Good
private static final int PASS_GRADE = 80;
if (grade > PASS_GRADE) { ... }
```

---

## 4. Project Architecture — Never Violate This

```
Console → Service → DBO → DBA → PostgreSQL
```

| Layer   | Responsibility                                      | Must NOT                        |
|---------|-----------------------------------------------------|---------------------------------|
| Console | Display output, receive input only                  | Call DBO or DBA directly        |
| Service | Business logic, use case flow                       | Call DBA directly               |
| DBO     | Execute DB queries (DAO role)                       | Contain business logic          |
| DBA     | PostgreSQL connection and shared DB access          | Contain business logic          |
| Model   | Domain state and behavior only                      | Contain any DB access code      |

**Every new feature must follow this flow without exception.**
Never skip layers. Never introduce a new architectural pattern without explicit approval.

---

## 5. Work Behavior Standards

### When receiving a request
1. Confirm the request is fully understood
2. Identify all affected classes and files
3. Check existing patterns in the codebase
4. Clarify scope before starting
5. After completion, provide a brief, clear summary of changes

### Never do
- Refactor code that wasn't part of the request
- Introduce new libraries or patterns without approval
- Delete or rename existing methods without confirmation
- Present uncertain code as if it were correct

### Always do
- Say "I'm not sure about this" when uncertain — never bluff
- For large changes, explain the plan first and get approval before coding
- Match existing code style exactly, without exception

---

## 6. Pre-Commit Self-Checklist

- [ ] Does the change follow Console → Service → DBO → DBA → PostgreSQL?
- [ ] Did I modify only what was requested?
- [ ] Does naming match the existing codebase conventions?
- [ ] Are there any empty catch blocks?
- [ ] Are all magic numbers and strings replaced with named constants?
- [ ] Does each method do exactly one thing?
- [ ] Is `db.properties` listed in `.gitignore`?
- [ ] Did I introduce any unrequested patterns or dependencies?

---

> These principles are not suggestions — they are constraints.
> If a request conflicts with these principles, flag it immediately and confirm before proceeding.

---

## 7. 프로젝트 기본 정보

- **시스템명:** 신동아화재 보험 관리 시스템
- **언어/환경:** 순수 Java (Spring / Maven / Gradle 없음)
- **빌드 명령:** `javac -encoding UTF-8 -cp "lib/*" "@sources_ascii.txt" -d out`
- **진입점:** `src/Main.java`
- **작업 브랜치:** `Gihyeon_code_3rd_ChanYoung`
- **DB:** PostgreSQL (`db.properties` — `.gitignore` 처리됨)

---

## 8. 현재 구현 완료 현황

**완료된 기능 (21개):**
보험청약 심사, 배서 관리, 부활 관리, 분납/수금 관리, 만기계약 관리,
사고 접수, 손해조사, 상품 개발, 제지급금 관리, 보상평가 관리, 협력업체 관리,
인수심사 이력, 인수심사 요청, 인수심사 결과, 사고 이력,
보험금 지급, 이의 신청, 대위 청구, 외주 요청,
피보험자, 계정

**미완료 (3개):** 공동인수, 재보험, 계약(Contract 기반 설계)

> 미완료 항목 작업 시 반드시 DB_TRANSITION_RULES.md 전체를 먼저 읽을 것.
