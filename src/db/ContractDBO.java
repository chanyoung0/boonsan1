package db;

import model.contract.Contract;
import java.util.ArrayList;
import java.util.List;

// Contract 엔티티 DB 매핑 — Contract 테이블 CRUD 담당
public class ContractDBO extends DBA {

    public Contract findByPolicyNumber(String policyNumber) {
        executeSelect("SELECT * FROM contract WHERE policy_number = '" + policyNumber + "'");
        return null;
    }

    public List<Contract> findAll() {
        executeSelect("SELECT * FROM contract");
        return new ArrayList<>();
    }

    public void save(Contract contract) {
        executeInsert("INSERT INTO contract (policy_number, contract_status, ...) VALUES (...)");
    }

    public void update(Contract contract) {
        executeUpdate("UPDATE contract SET contract_status = ... WHERE policy_number = '" + contract.getPolicyNumber() + "'");
    }

    public void delete(String policyNumber) {
        executeDelete("DELETE FROM contract WHERE policy_number = '" + policyNumber + "'");
    }
}
