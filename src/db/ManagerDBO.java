package db;

import model.person.Manager;
import java.util.ArrayList;
import java.util.List;

// Manager 엔티티 DB 매핑 — manager 테이블 CRUD 담당
public class ManagerDBO extends DBA {

    public Manager findByEmployeeNo(String employeeNo) {
        executeSelect("SELECT * FROM manager WHERE employee_no = '" + employeeNo + "'");
        return null;
    }

    public List<Manager> findAll() {
        executeSelect("SELECT * FROM manager");
        return new ArrayList<>();
    }

    public void save(Manager manager) {
        executeInsert("INSERT INTO manager (...) VALUES (...)");
    }

    public void update(Manager manager) {
        executeUpdate("UPDATE manager SET ... WHERE employee_no = ...");
    }

    public void delete(String employeeNo) {
        executeDelete("DELETE FROM manager WHERE employee_no = '" + employeeNo + "'");
    }
}
