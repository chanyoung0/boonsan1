package repository.inmemory;

import model.accident.DamageInvestigation;
import repository.DamageInvestigationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 손해조사 인메모리 저장소.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryDamageInvestigationRepository implements DamageInvestigationRepository {

    private final List<DamageInvestigation> store = new ArrayList<>();

    @Override
    public Optional<DamageInvestigation> findByInvestigationId(String investigationId) {
        return store.stream().filter(d -> investigationId.equals(d.getInvestigationId())).findFirst();
    }

    @Override
    public List<DamageInvestigation> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public DamageInvestigation save(DamageInvestigation investigation) {
        store.add(investigation);
        return investigation;
    }
}
