package repository.inmemory;

import enums.EvaluationGrade;
import model.partner.Partner;
import repository.PartnerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 협력업체 인메모리 저장소 — 손해조사 위탁용 협력업체 시드 보유.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryPartnerRepository implements PartnerRepository {

    private final List<Partner> store = new ArrayList<>();

    // 손해조사 위탁용 협력업체 3곳으로 초기화
    public InMemoryPartnerRepository() {
        store.add(new Partner("P-001", "삼성손해사정", "ADJUSTER", "02-1111-2222", "손해사정 전문", EvaluationGrade.EXCELLENT));
        store.add(new Partner("P-002", "현대정비공장", "REPAIR", "02-3333-4444", "차량 수리 전문", EvaluationGrade.GOOD));
        store.add(new Partner("P-003", "강남병원", "MEDICAL", "02-5555-6666", "의료 심사", EvaluationGrade.GOOD));
    }

    @Override
    public Optional<Partner> findById(String id) {
        return store.stream().filter(p -> id.equals(p.getId())).findFirst();
    }

    @Override
    public List<Partner> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public Partner save(Partner partner) {
        store.add(partner);
        return partner;
    }
}
