package contract.dto;

import enums.ContractStatus;

import java.time.LocalDateTime;

public class MaturityRenewalResponse {

    private final String policyNumber;
    private final Boolean renewalIntention;
    private final ContractStatus contractStatus;
    private final LocalDateTime checkedAt;
    private final String message;

    public MaturityRenewalResponse(
            String policyNumber,
            Boolean renewalIntention,
            ContractStatus contractStatus,
            LocalDateTime checkedAt,
            String message
    ) {
        this.policyNumber = policyNumber;
        this.renewalIntention = renewalIntention;
        this.contractStatus = contractStatus;
        this.checkedAt = checkedAt;
        this.message = message;
    }

    public String getPolicyNumber() { return policyNumber; }
    public Boolean getRenewalIntention() { return renewalIntention; }
    public ContractStatus getContractStatus() { return contractStatus; }
    public LocalDateTime getCheckedAt() { return checkedAt; }
    public String getMessage() { return message; }
}
