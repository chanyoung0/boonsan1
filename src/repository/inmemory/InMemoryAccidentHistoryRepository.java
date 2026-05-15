package repository.inmemory;

import enums.AccidentType;
import model.accident.AccidentHistory;
import repository.AccidentHistoryRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사고 이력 인메모리 저장소 — 신용정보 조회 시뮬레이션용 시드 데이터 보유.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryAccidentHistoryRepository implements AccidentHistoryRepository {

    private final List<AccidentHistory> store = new ArrayList<>();

    // ICIS 응답 시뮬레이션을 위한 사고 이력 시드
    public InMemoryAccidentHistoryRepository() {
        AccidentHistory h = new AccidentHistory(
                "ACC-2022-003481",
                AccidentType.VEHICLE_ACCIDENT,
                LocalDateTime.of(2022, 5, 9, 16, 30),
                new BigDecimal("1500000"),
                new BigDecimal("1200000"));
        h.setLocation("서울시 강남구 테헤란로 123번지");
        h.setDiagnosisName("경추 염좌");
        h.setDiagnosisCode("S13.4");
        h.setTreatmentDetails("물리치료 12회, 약물치료 2주");
        h.setPaidAt(LocalDateTime.of(2022, 6, 15, 0, 0));
        store.add(h);
    }

    @Override
    public List<AccidentHistory> findByPersonRRN(String rrn) {
        return new ArrayList<>(store);
    }

    @Override
    public List<AccidentHistory> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public AccidentHistory save(AccidentHistory history) {
        store.add(history);
        return history;
    }
}
