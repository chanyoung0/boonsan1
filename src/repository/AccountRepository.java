package repository;

import model.person.Account;

import java.util.List;
import java.util.Optional;

// 계좌 저장소 인터페이스
public interface AccountRepository {

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByHolder(String holderName);

    List<Account> findAll();

    Account save(Account account);
}
