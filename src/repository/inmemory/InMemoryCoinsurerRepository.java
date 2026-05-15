package repository.inmemory;

import model.underwriting.Coinsurer;
import repository.CoinsurerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 공동인수사 인메모리 저장소 — 7개 후보 보험사 시드 데이터 보유.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryCoinsurerRepository implements CoinsurerRepository {

    private final List<Coinsurer> store = new ArrayList<>();

    // 공동인수 가능 보험사 후보 7곳으로 초기화
    public InMemoryCoinsurerRepository() {
        store.add(approvable("삼성화재", 40f));
        store.add(approvable("DB손해보험", 30f));
        store.add(approvable("현대해상", 35f));
        store.add(approvable("한화손해보험", 25f));
        store.add(approvable("롯데손해보험", 20f));
        Coinsurer mg = new Coinsurer("MG손해보험", 0f, 0f);
        mg.setApproved(false);
        store.add(mg);
        store.add(approvable("흥국화재", 15f));
    }

    private Coinsurer approvable(String name, float maxShare) {
        Coinsurer c = new Coinsurer(name, 0f, maxShare);
        c.setApproved(true);
        return c;
    }

    @Override
    public Optional<Coinsurer> findByCompanyName(String companyName) {
        return store.stream().filter(c -> companyName.equals(c.getCompanyName())).findFirst();
    }

    @Override
    public List<Coinsurer> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public List<Coinsurer> findApprovableCandidates() {
        List<Coinsurer> result = new ArrayList<>();
        for (Coinsurer c : store) {
            if (c.getMaxAcceptableShareRate() > 0f) result.add(c);
        }
        return result;
    }

    @Override
    public Coinsurer save(Coinsurer coinsurer) {
        store.add(coinsurer);
        return coinsurer;
    }
}
