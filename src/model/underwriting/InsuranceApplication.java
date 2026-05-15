package model.underwriting;

import enums.ApplicationStatus;
import enums.SpecialContractType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 청약 도메인 모델 — 보험 가입 신청 정보 관리
public class InsuranceApplication {

    private String applicationId;
    private ApplicationStatus applicationStatus;
    private LocalDateTime appliedAt;
    private String appliedCondition;
    private String productCode;
    private String insuredPersonInfo;
    private BigDecimal insuredAmount;
    private BigDecimal premium;
    private String paymentCycle;
    private SpecialContractType specialContractList;
    private String termsVersion;

    public InsuranceApplication() {}

    // 청약 기본 정보로 초기화
    public InsuranceApplication(String applicationId, String productCode, String insuredPersonInfo,
                                BigDecimal insuredAmount, BigDecimal premium, String paymentCycle) {
        this.applicationId = applicationId;
        this.productCode = productCode;
        this.insuredPersonInfo = insuredPersonInfo;
        this.insuredAmount = insuredAmount;
        this.premium = premium;
        this.paymentCycle = paymentCycle;
        this.applicationStatus = ApplicationStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
    }

    // 청약 상태 변경
    public void changeApplicationStatus() {}

    // 청약 확정
    public void confirmApplication() {}

    // 증권번호 발급
    public void issuePolicyNumber() {}

    // 청약 접수 — 채번된 청약번호 반환
    public String receiveApplication() {
        this.applicationStatus = ApplicationStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
        return this.applicationId;
    }

    public String getApplicationId() { return applicationId; }
    public ApplicationStatus getApplicationStatus() { return applicationStatus; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public String getAppliedCondition() { return appliedCondition; }
    public String getProductCode() { return productCode; }
    public String getInsuredPersonInfo() { return insuredPersonInfo; }
    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public BigDecimal getPremium() { return premium; }
    public String getPaymentCycle() { return paymentCycle; }
    public SpecialContractType getSpecialContractList() { return specialContractList; }
    public String getTermsVersion() { return termsVersion; }

    public void setApplicationId(String s) { this.applicationId = s; }
    public void setApplicationStatus(ApplicationStatus s) { this.applicationStatus = s; }
    public void setAppliedAt(LocalDateTime t) { this.appliedAt = t; }
    public void setAppliedCondition(String s) { this.appliedCondition = s; }
    public void setProductCode(String s) { this.productCode = s; }
    public void setInsuredPersonInfo(String s) { this.insuredPersonInfo = s; }
    public void setInsuredAmount(BigDecimal v) { this.insuredAmount = v; }
    public void setPremium(BigDecimal v) { this.premium = v; }
    public void setPaymentCycle(String s) { this.paymentCycle = s; }
    public void setSpecialContractList(SpecialContractType t) { this.specialContractList = t; }
    public void setTermsVersion(String s) { this.termsVersion = s; }

    @Override
    public String toString() {
        return "InsuranceApplication{id='" + applicationId + "', status=" + applicationStatus
                + ", amount=" + insuredAmount + "}";
    }
}
