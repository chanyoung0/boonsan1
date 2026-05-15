package repository.inmemory;

import enums.ContractStatus;
import enums.PaymentCycle;
import model.contract.Contract;
import repository.ContractRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 계약 인메모리 저장소 — 하드코딩 시드 데이터를 보유.
 * TODO: Replace with JPA-backed implementation when DB is wired.
 */
public class InMemoryContractRepository implements ContractRepository {

    private final List<Contract> store = new ArrayList<>();

    // 시나리오에 사용되는 하드코딩 계약 시드로 초기화
    public InMemoryContractRepository() {
        store.add(new Contract("P2023-004512", ContractStatus.ACTIVE, PaymentCycle.MONTHLY, 12, false));
        store.add(new Contract("P2024-001234", ContractStatus.ACTIVE, PaymentCycle.MONTHLY, 12, false));
        store.add(new Contract("P2024-005678", ContractStatus.ACTIVE, PaymentCycle.MONTHLY, 12, false));
        store.add(new Contract("P2024-009012", ContractStatus.ACTIVE, PaymentCycle.MONTHLY, 12, true));
        store.add(new Contract("P2019-000123", ContractStatus.EXPIRED, PaymentCycle.MONTHLY, 60, false));
        store.add(new Contract("P2019-000456", ContractStatus.EXPIRED, PaymentCycle.MONTHLY, 60, false));
    }

    @Override
    public Optional<Contract> findByPolicyNumber(String policyNumber) {
        return store.stream().filter(c -> policyNumber.equals(c.getPolicyNumber())).findFirst();
    }

    @Override
    public List<Contract> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public List<Contract> findDueOn(LocalDate dueDate) {
        List<Contract> result = new ArrayList<>();
        for (Contract c : store) {
            if (c.getContractStatus() == ContractStatus.ACTIVE) result.add(c);
        }
        return result;
    }

    @Override
    public List<Contract> findMaturingOn(LocalDate maturityDate) {
        List<Contract> result = new ArrayList<>();
        for (Contract c : store) {
            if (c.getContractStatus() == ContractStatus.EXPIRED) result.add(c);
        }
        return result;
    }

    @Override
    public Contract save(Contract contract) {
        store.add(contract);
        return contract;
    }
}
