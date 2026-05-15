package repository.inmemory;

import model.accident.InsurancePayment;
import repository.InsurancePaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 보험금 지급 인메모리 저장소.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryInsurancePaymentRepository implements InsurancePaymentRepository {

    private final List<InsurancePayment> store = new ArrayList<>();

    @Override
    public Optional<InsurancePayment> findByPaymentId(String paymentId) {
        return store.stream().filter(p -> paymentId.equals(p.getPaymentId())).findFirst();
    }

    @Override
    public List<InsurancePayment> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public InsurancePayment save(InsurancePayment payment) {
        store.add(payment);
        return payment;
    }
}
