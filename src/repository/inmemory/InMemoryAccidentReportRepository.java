package repository.inmemory;

import model.accident.AccidentReport;
import repository.AccidentReportRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 사고 접수 인메모리 저장소.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryAccidentReportRepository implements AccidentReportRepository {

    private final List<AccidentReport> store = new ArrayList<>();

    @Override
    public Optional<AccidentReport> findByReportNo(String reportNo) {
        return store.stream().filter(r -> reportNo.equals(r.getReportNo())).findFirst();
    }

    @Override
    public List<AccidentReport> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public AccidentReport save(AccidentReport report) {
        store.add(report);
        return report;
    }
}
