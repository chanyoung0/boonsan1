package db;

import model.person.Account;

import java.util.List;

public interface AccountMapper {

    Account findById(String accountNumber);

    List<Account> findAll();

    int insert(Account account);
}
