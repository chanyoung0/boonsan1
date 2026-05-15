package db;

import model.person.InsuredPerson;
import java.util.ArrayList;
import java.util.List;

// InsuredPerson 엔티티 DB 매핑 — insured_person 테이블 CRUD 담당
public class InsuredPersonDBO extends DBA {

    public InsuredPerson findByRrn(String residentRegistrationNumber) {
        executeSelect("SELECT * FROM insured_person WHERE rrn = '" + residentRegistrationNumber + "'");
        return null;
    }

    public List<InsuredPerson> findAll() {
        executeSelect("SELECT * FROM insured_person");
        return new ArrayList<>();
    }

    public void save(InsuredPerson person) {
        executeInsert("INSERT INTO insured_person (...) VALUES (...)");
    }

    public void update(InsuredPerson person) {
        executeUpdate("UPDATE insured_person SET ... WHERE rrn = ...");
    }

    public void delete(String rrn) {
        executeDelete("DELETE FROM insured_person WHERE rrn = '" + rrn + "'");
    }
}
