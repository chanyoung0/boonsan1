# Stub DBO 인벤토리 메모

> **컨텍스트**: `MyBatis_migration_1st` 브랜치에서는 *활성 13개 DBO만* MyBatis로 전환했다. 나머지 17개는 어디서도 인스턴스화되지 않는 죽은 코드이고, 가리키는 테이블도 `schema.sql`에 없다. 본 메모는 이들을 정리할 때 참고할 인벤토리이다.
>
> **삭제 가능성**: 모두 dead code. 단순 삭제로 충분하다. 단, 해당 도메인을 *나중에* DB 영속화할 계획이 있다면 그때 새로 작성하는 편이 낫다 (현재 stub은 형식적이라 참고 가치 거의 없음).

---

## 1. Stub DBO 목록 (17개)

모두 `DBA`를 상속하고 `executeSelect/Insert/Update/Delete`(`println` 스텁)를 호출만 한다. `findById/findAll`은 `null` 또는 빈 `ArrayList`를 반환한다.

| DBO 파일 | 가리키는 테이블 (schema.sql에 없음) | 관련 모델 |
|---|---|---|
| `AccidentHistoryDBO` | `accident_history` | `model.accident.AccidentHistory` |
| `AccountDBO` | `account` | `model.person.Account` |
| `CoinsuranceDBO` | `coinsurance` | `model.underwriting.Coinsurance` |
| `CoinsurerDBO` | `coinsurer` | `model.underwriting.Coinsurer` |
| `ContractDBO` | `contract` | `model.contract.Contract` |
| `DocumentDBO` | `document` | `model.document.Document` |
| `InsurancePaymentDBO` | `insurance_payment` | `model.accident.InsurancePayment` |
| `InsuredPersonDBO` | `insured_person` | `model.person.InsuredPerson` |
| `ManagerDBO` | `manager` | `model.person.Manager` |
| `ObjectionDBO` | `objection` | `model.accident.Objection` |
| `OutsourceRequestDBO` | `outsource_request` | `model.accident.OutsourceRequest` |
| `ReinsuranceDBO` | `reinsurance` | `model.underwriting.Reinsurance` |
| `SubrogationDBO` | `subrogation` | `model.accident.Subrogation` |
| `TransferDBO` | `transfer` | `model.contract.Transfer` |
| `UnderwritingHistoryDBO` | `underwriting_history` | `model.underwriting.UnderwritingHistory` |
| `UnderwritingRequestDBO` | `underwriting_request` | `model.underwriting.UnderwritingRequest` |
| `UnderwritingResultDBO` | `underwriting_result` | `model.underwriting.UnderwritingResult` |

## 2. 호출처

`Grep "new \w+DBO\(\)"`로 전체 검증 — 위 17개 중 어느 것도 `new XxxDBO()` 형태로 인스턴스화된 곳이 **없다**. 활성 DBO 13개만 Service 계층에서 사용 중.

```
$ grep -RIn "new \(AccidentHistory\|Account\|Coinsurance\|Coinsurer\|Contract\|Document\|InsurancePayment\|InsuredPerson\|Manager\|Objection\|OutsourceRequest\|Reinsurance\|Subrogation\|Transfer\|UnderwritingHistory\|UnderwritingRequest\|UnderwritingResult\)DBO()" src/
# (zero results)
```

## 3. 영향도 분석 — 삭제해도 되는가?

- ✅ Service/Console/Model에서 *어떤 import도 없음*.
- ✅ schema.sql에 대응 테이블이 없으므로 SQL 실행도 불가능 (try-catch 안에서 print만 함).
- ✅ DBA의 `executeXxx` 메서드는 이들만 사용 — stub DBO를 삭제하면 DBA 정리도 가능.
- ⚠️ `Contract` 모델은 살아있고 in-memory aggregation에서 사용 중. 모델은 유지하되 DBO만 삭제.

## 4. 삭제 권장 순서

1. **17개 stub DBO 파일 삭제** (단순 `git rm`)
2. `DBA.java`에서 dead method 정리:
   - `connect/disconnect/login` 삭제
   - `executeSelect/Insert/Update/Delete` 삭제
   - `loadDbProperties/requireProperty` 삭제 (이제 `MyBatisSessionFactory`가 담당)
   - `getConnection()`도 활성 DBO에서 호출하지 않으므로 삭제 가능
   - 즉 `DBA` 클래스 자체를 삭제 가능
3. 활성 13개 DBO의 `extends DBA` 제거 (또는 새 base class `db.AbstractDBO`로 교체)

### 단순화 옵션
한 PR로 묶으려면:
- `git rm src/main/java/db/{AccidentHistory,Account,Coinsurance,Coinsurer,Contract,Document,InsurancePayment,InsuredPerson,Manager,Objection,OutsourceRequest,Reinsurance,Subrogation,Transfer,UnderwritingHistory,UnderwritingRequest,UnderwritingResult}DBO.java`
- `git rm src/main/java/db/DBA.java`
- 13개 활성 DBO에서 `extends DBA` 제거
- compile 확인 (Service에 영향 없을 것)

## 5. 향후 *실제* 영속화 계획이 있다면

위 도메인 중 다음은 클래스 다이어그램의 Aggregation 관계 측면에서 *진짜로 DB 저장이 필요*해질 가능성이 있음:

| 도메인 | 영속화 시 우선순위 | 사유 |
|---|---|---|
| `Contract` | 🔴 매우 높음 | 모든 계약 관련 처리의 중심 객체. 현재 `PayoutService.resolveContract`가 매번 새 인스턴스를 만드는 핫픽스 상태 |
| `InsurancePayment` | 🟠 높음 | 손해조사 → 지급 흐름의 결과물. 시나리오는 DB 저장을 명시 |
| `AccidentDocument`/`Document` | 🟠 높음 | 사고 접수 시나리오 Basic Path 6번에 명시적 DB 저장 |
| `OutsourceRequest` | 🟡 중간 | 손해조사 위탁 흐름 |
| `Objection`, `Subrogation` | 🟡 중간 | 보상 처리 후속 흐름 |
| `Coinsurance`, `Coinsurer`, `Reinsurance` | 🟢 낮음 | 외부 시스템 모킹과 함께 다룸 |
| `UnderwritingHistory`, `AccidentHistory` | 🟢 낮음 | 신용정보원/ICIS 모킹에서 다룸 |
| `Account`, `InsuredPerson`, `Manager` | 🟢 낮음 | 다른 엔티티의 부속 정보 |
| `Transfer`, `UnpaidNotice` | 🟢 낮음 | 분납/수금 부속 처리 |

→ 우선순위 🔴/🟠 항목들은 schema에 테이블을 새로 추가하고 MyBatis Mapper를 작성하는 별도 PR로 분리할 것.

## 6. 정리 PR 체크리스트

- [ ] 17개 stub DBO 파일 `git rm`
- [ ] `DBA.java` 삭제 또는 dead method 제거
- [ ] 13개 활성 DBO에서 `extends DBA` 제거 (DBA 삭제 시)
- [ ] `gradle build` 통과 확인
- [ ] `gradle mybatisSmoke` 통과 확인
- [ ] git log 메시지: `cleanup: remove unused stub DBOs and DBA dead methods`
