package db;

import model.accident.AccidentReport;
import java.util.ArrayList;
import java.util.List;

// AccidentReport 엔티티 DB 매핑 — accident_report 테이블 CRUD 담당
public class AccidentReportDBO extends DBA {

    public AccidentReport findByReportNo(String reportNo) {
        executeSelect("SELECT * FROM accident_report WHERE report_no = '" + reportNo + "'");
        return null;
    }

    public List<AccidentReport> findAll() {
        executeSelect("SELECT * FROM accident_report");
        return new ArrayList<>();
    }

    public void save(AccidentReport report) {
        executeInsert("INSERT INTO accident_report (report_no, accident_status, ...) VALUES (...)");
    }

    public void update(AccidentReport report) {
        executeUpdate("UPDATE accident_report SET accident_status = ... WHERE report_no = '" + report.getReportNo() + "'");
    }

    public void delete(String reportNo) {
        executeDelete("DELETE FROM accident_report WHERE report_no = '" + reportNo + "'");
    }
}
