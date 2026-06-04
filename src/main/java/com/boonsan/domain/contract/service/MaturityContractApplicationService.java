package com.boonsan.domain.contract.service;

import com.boonsan.domain.contract.dto.MaturityNoticeResponse;
import com.boonsan.domain.contract.dto.MaturityProcessResponse;
import com.boonsan.domain.contract.mapper.MaturityContractMapper;
import com.boonsan.domain.enums.ContractStatus;
import com.boonsan.domain.model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class MaturityContractApplicationService {

    private static final String DEFAULT_DELIVERY_METHOD = "SMS";

    private final ContractApplicationService contractApplicationService;
    private final MaturityContractMapper maturityContractMapper;

    public MaturityContractApplicationService(
            ContractApplicationService contractApplicationService,
            MaturityContractMapper maturityContractMapper
    ) {
        this.contractApplicationService = contractApplicationService;
        this.maturityContractMapper = maturityContractMapper;
    }

    @Transactional(readOnly = true)
    public MaturityNoticeResponse getMaturityNotice(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        LocalDate today = LocalDate.now();
        long daysUntilMaturity = ChronoUnit.DAYS.between(today, contract.getContractEndDate());
        String noticeMessage = composeNoticeMessage(contract.getContractStatus(), daysUntilMaturity, contract.getContractEndDate());

        return new MaturityNoticeResponse(
                contract.getPolicyNumber(),
                contract.getInsuredName(),
                contract.getInsuredContact(),
                contract.getContractEndDate(),
                contract.getContractStatus(),
                daysUntilMaturity,
                noticeMessage,
                DEFAULT_DELIVERY_METHOD
        );
    }

    @Transactional
    public MaturityProcessResponse processMaturity(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        ContractStatus previousStatus = contract.getContractStatus();

        if (previousStatus != ContractStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Maturity processing requires ACTIVE contract, but current status: " + previousStatus);
        }

        LocalDate today = LocalDate.now();
        if (contract.getContractEndDate().isAfter(today)) {
            throw new IllegalArgumentException(
                    "Maturity processing requires contractEndDate <= today: " + contract.getContractEndDate());
        }

        int updated = maturityContractMapper.updateStatusToExpired(
                contract.getPolicyNumber(),
                ContractStatus.ACTIVE.name(),
                ContractStatus.EXPIRED.name()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Maturity processing failed (concurrent modification): " + contract.getPolicyNumber());
        }

        return new MaturityProcessResponse(
                contract.getPolicyNumber(),
                previousStatus,
                ContractStatus.EXPIRED,
                contract.getContractEndDate(),
                LocalDateTime.now(),
                "만기 처리가 완료되었습니다."
        );
    }

    private String composeNoticeMessage(ContractStatus status, long daysUntilMaturity, LocalDate endDate) {
        if (status == ContractStatus.EXPIRED) {
            return "이미 만기 처리된 계약입니다. (만기일 " + endDate + ")";
        }
        if (daysUntilMaturity > 30) {
            return "만기일(" + endDate + ")까지 " + daysUntilMaturity + "일 남았습니다.";
        }
        if (daysUntilMaturity > 0) {
            return "만기일(" + endDate + ")이 " + daysUntilMaturity + "일 앞으로 다가왔습니다. 만기 처리 안내드립니다.";
        }
        if (daysUntilMaturity == 0) {
            return "오늘이 만기일(" + endDate + ")입니다. 만기 처리를 진행해 주세요.";
        }
        return "만기일(" + endDate + ")이 " + Math.abs(daysUntilMaturity) + "일 경과되었습니다. 즉시 만기 처리가 필요합니다.";
    }
}
