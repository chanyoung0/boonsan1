package db;

import model.person.Account;

// 계좌 MyBatis Mapper
public interface AccountMapper {

    Account findByAccountNumber(String accountNumber);

    int insert(Account account);
}
