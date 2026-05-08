package repository;

import model.contract.Contract;

import java.util.ArrayList;
import java.util.List;

// 계약 메모리 저장소 — Contract 객체를 List로 보관하고 조회를 제공
public class ContractRepository {

    private static final List<Contract> store = new ArrayList<>();

    public static void save(Contract contract) {
        store.add(contract);
    }

    public static Contract findByPolicyNo(String policyNo) {
        for (Contract c : store) {
            if (c.getPolicyNo().equals(policyNo)) return c;
        }
        return null;
    }

    public static List<Contract> findAll() {
        return new ArrayList<>(store);
    }
}
