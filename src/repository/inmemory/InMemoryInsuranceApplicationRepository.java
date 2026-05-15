package repository.inmemory;

import model.underwriting.InsuranceApplication;
import repository.InsuranceApplicationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 청약 인메모리 저장소 — 시나리오 중 채번된 청약을 보관.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryInsuranceApplicationRepository implements InsuranceApplicationRepository {

    private final List<InsuranceApplication> store = new ArrayList<>();

    @Override
    public Optional<InsuranceApplication> findByApplicationId(String applicationId) {
        return store.stream().filter(a -> applicationId.equals(a.getApplicationId())).findFirst();
    }

    @Override
    public List<InsuranceApplication> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public InsuranceApplication save(InsuranceApplication application) {
        store.add(application);
        return application;
    }
}
