package underwriting;

import enums.ApplicationStatus;
import enums.AppliedCondition;
import enums.SpecialContractType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InsuranceApplication {

    private String applicationId;
    private ApplicationStatus applicationStatus;
    private LocalDateTime appliedAt;
    private AppliedCondition appliedCondition;
    private BigDecimal insuredAmount;
    private String insuredPersonInfo;
    private String paymentCycle;
    private BigDecimal premium;
    private String productCode;
    private SpecialContractType specialContractList;
    private String termVersion;

    public void changeApplicationStatus() {}

    public void notifyInsuredPerson() {}

    public void issuePolicyNumber() {}

    public void receiveApplication() {}
}
