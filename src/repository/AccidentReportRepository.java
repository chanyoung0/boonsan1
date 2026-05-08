package repository;

import model.accident.AccidentReport;

import java.util.ArrayList;
import java.util.List;

// 사고접수 메모리 저장소 — AccidentReport 객체를 List로 보관하고 조회를 제공
public class AccidentReportRepository {

    private static final List<AccidentReport> store = new ArrayList<>();

    public static void save(AccidentReport report) {
        store.add(report);
    }

    public static AccidentReport findByReportNo(String reportNo) {
        for (AccidentReport r : store) {
            if (r.getReportNo().equals(reportNo)) return r;
        }
        return null;
    }

    public static List<AccidentReport> findAll() {
        return new ArrayList<>(store);
    }
}
