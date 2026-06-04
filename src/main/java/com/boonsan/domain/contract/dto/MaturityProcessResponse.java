package com.boonsan.domain.contract.dto;

import com.boonsan.domain.enums.ContractStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaturityProcessResponse {

    private final String policyNumber;
    private final ContractStatus previousStatus;
    private final ContractStatus contractStatus;
    private final LocalDate contractEndDate;
    private final LocalDateTime processedAt;
    private final String message;

    public MaturityProcessResponse(
            String policyNumber,
            ContractStatus previousStatus,
            ContractStatus contractStatus,
            LocalDate contractEndDate,
            LocalDateTime processedAt,
            String message
    ) {
        this.policyNumber = policyNumber;
        this.previousStatus = previousStatus;
        this.contractStatus = contractStatus;
        this.contractEndDate = contractEndDate;
        this.processedAt = processedAt;
        this.message = message;
    }

    public String getPolicyNumber() { return policyNumber; }

    public ContractStatus getPreviousStatus() { return previousStatus; }

    public ContractStatus getContractStatus() { return contractStatus; }

    public LocalDate getContractEndDate() { return contractEndDate; }

    public LocalDateTime getProcessedAt() { return processedAt; }

    public String getMessage() { return message; }
}
