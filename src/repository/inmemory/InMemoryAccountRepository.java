package repository.inmemory;

import enums.AccountType;
import enums.BankName;
import model.person.Account;
import repository.AccountRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 계좌 인메모리 저장소 — 자동이체/지급 시나리오용 계좌 시드 보유.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryAccountRepository implements AccountRepository {

    private final List<Account> store = new ArrayList<>();

    // 시나리오 등장 계좌 2개로 초기화
    public InMemoryAccountRepository() {
        store.add(new Account("홍길동", "110-123-456789", AccountType.AUTO_TRANSFER, BankName.SHINHAN, new BigDecimal("3000000")));
        store.add(new Account("이철수", "123-456-789012", AccountType.AUTO_TRANSFER, BankName.KB, new BigDecimal("1500000")));
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return store.stream().filter(a -> accountNumber.equals(a.getAccountNumber())).findFirst();
    }

    @Override
    public Optional<Account> findByHolder(String holderName) {
        return store.stream().filter(a -> holderName.equals(a.getAccountHolder())).findFirst();
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public Account save(Account account) {
        store.add(account);
        return account;
    }
}
