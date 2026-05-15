package repository;

import model.accident.AccidentHistory;

import java.util.List;

// 사고 이력 저장소 인터페이스
public interface AccidentHistoryRepository {

    List<AccidentHistory> findByPersonRRN(String residentRegistrationNumber);

    List<AccidentHistory> findAll();

    AccidentHistory save(AccidentHistory history);
}
