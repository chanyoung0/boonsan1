package db;

import model.person.Account;
import java.util.ArrayList;
import java.util.List;

// Account 엔티티 DB 매핑 — account 테이블 CRUD 담당
public class AccountDBO extends DBA {

    public Account findByAccountNumber(String accountNumber) {
        executeSelect("SELECT * FROM account WHERE account_number = '" + accountNumber + "'");
        return null;
    }

    public List<Account> findAll() {
        executeSelect("SELECT * FROM account");
        return new ArrayList<>();
    }

    public void save(Account account) {
        executeInsert("INSERT INTO account (...) VALUES (...)");
    }

    public void update(Account account) {
        executeUpdate("UPDATE account SET ... WHERE account_number = ...");
    }

    public void delete(String accountNumber) {
        executeDelete("DELETE FROM account WHERE account_number = '" + accountNumber + "'");
    }
}
