package repository;

import model.accident.AccidentReport;

import java.util.List;
import java.util.Optional;

// 사고 접수 저장소 인터페이스
public interface AccidentReportRepository {

    Optional<AccidentReport> findByReportNo(String reportNo);

    List<AccidentReport> findAll();

    AccidentReport save(AccidentReport report);
}
