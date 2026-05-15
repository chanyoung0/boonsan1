package repository;

import model.contract.Contract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// 계약 저장소 인터페이스 — 구현체는 인메모리 또는 추후 JPA로 교체 가능
public interface ContractRepository {

    Optional<Contract> findByPolicyNumber(String policyNumber);

    List<Contract> findAll();

    List<Contract> findDueOn(LocalDate dueDate);

    List<Contract> findMaturingOn(LocalDate maturityDate);

    Contract save(Contract contract);
}
