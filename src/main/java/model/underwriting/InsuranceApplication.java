package model.underwriting;

import enums.ApplicationStatus;
import enums.SpecialContractType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InsuranceApplication {

    private String applicationId;
    private ApplicationStatus applicationStatus;
    private LocalDateTime appliedAt;
    private String appliedCondition;
    private BigDecimal insuredAmount;
    private String insuredPersonInfo;
    private String paymentCycle;
    private BigDecimal premium;
    private String productCode;
    private SpecialContractType specialContractList;
    private String termsVersion;

    public void changeApplicationStatus() {}

    public void confirmApplication() {}

    public void issuePolicyNumber() {}

    public String receiveApplication() { return applicationId; }

    public String getApplicationId() { return applicationId; }
}
