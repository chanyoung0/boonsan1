package repository.inmemory;

import enums.AccountType;
import enums.BankName;
import model.person.Account;
import model.person.InsuredPerson;
import repository.InsuredPersonRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 피보험자 인메모리 저장소 — 시나리오용 더미 인물 보유.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryInsuredPersonRepository implements InsuredPersonRepository {

    private final List<InsuredPerson> store = new ArrayList<>();

    // 시나리오 등장 인물 5명으로 초기화
    public InMemoryInsuredPersonRepository() {
        Account hongAcc = new Account("홍길동", "110-123-456789", AccountType.AUTO_TRANSFER, BankName.SHINHAN, new BigDecimal("3000000"));
        store.add(new InsuredPerson("홍길동", "890712-1234567", "010-9876-5432", hongAcc));
        store.add(new InsuredPerson("김영희", "920304-2345678", "010-1111-2222", null));
        store.add(new InsuredPerson("이철수", "850315-1567890", "010-1234-5678",
                new Account("이철수", "123-456-789012", AccountType.AUTO_TRANSFER, BankName.KB, new BigDecimal("1500000"))));
        store.add(new InsuredPerson("박민준", "880201-1888888", "010-3333-4444", null));
        store.add(new InsuredPerson("최수진", "910808-2999999", "010-5555-6666", null));
    }

    @Override
    public Optional<InsuredPerson> findByRRN(String rrn) {
        return store.stream().filter(p -> rrn.equals(p.getResidentRegistrationNumber())).findFirst();
    }

    @Override
    public Optional<InsuredPerson> findByName(String name) {
        return store.stream().filter(p -> name.equals(p.getName())).findFirst();
    }

    @Override
    public List<InsuredPerson> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public InsuredPerson save(InsuredPerson person) {
        store.add(person);
        return person;
    }
}
