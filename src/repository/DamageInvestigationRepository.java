package repository;

import model.accident.DamageInvestigation;

import java.util.List;
import java.util.Optional;

// 손해조사 저장소 인터페이스
public interface DamageInvestigationRepository {

    Optional<DamageInvestigation> findByInvestigationId(String investigationId);

    List<DamageInvestigation> findAll();

    DamageInvestigation save(DamageInvestigation investigation);
}
