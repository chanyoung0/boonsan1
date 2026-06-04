package contract.service;

import contract.dto.ContractResponse;
import contract.mapper.ContractMapper;
import model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ContractApplicationService {

    private final ContractMapper contractMapper;

    public ContractApplicationService(ContractMapper contractMapper) {
        this.contractMapper = contractMapper;
    }

    @Transactional(readOnly = true)
    public ContractResponse findByPolicyNumber(String policyNumber) {
        Contract contract = requireContract(policyNumber);
        return ContractResponse.from(contract);
    }

    // 후속 슬라이스(만기/제지급금/분납수금/부활/배서)에서 재사용할 헬퍼
    public Contract requireContract(String policyNumber) {
        String normalized = requireText(policyNumber, "policyNumber");
        Contract contract = contractMapper.findByPolicyNumber(normalized);
        if (contract == null) {
            throw new NoSuchElementException("Contract not found: " + normalized);
        }
        return contract;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }
}
