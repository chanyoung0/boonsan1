package model.underwriting;

import enums.ApplicationStatus;
import enums.PaymentCycle;
import enums.SpecialContractType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 보험 청약 도메인 모델 — 보험 가입 신청 정보 관리
public class InsuranceApplication {

    private String applicationId;
    private ApplicationStatus applicationStatus;
    private LocalDateTime appliedAt;
    private String appliedCondition;
    private BigDecimal insuredAmount;
    private String insuredPersonInfo;
    private PaymentCycle paymentCycle;
    private BigDecimal premium;
    private String productCode;
    private SpecialContractType specialContractList;
    private String termsVersion;

    public InsuranceApplication() {}

    public InsuranceApplication(String productCode, String insuredPersonInfo, BigDecimal insuredAmount,
                                BigDecimal premium, PaymentCycle paymentCycle, SpecialContractType specialContractList,
                                String termsVersion, String appliedCondition) {
        this.productCode = productCode;
        this.insuredPersonInfo = insuredPersonInfo;
        this.insuredAmount = insuredAmount;
        this.premium = premium;
        this.paymentCycle = paymentCycle;
        this.specialContractList = specialContractList;
        this.termsVersion = termsVersion;
        this.appliedCondition = appliedCondition;
    }

    // 청약 상태 변경
    public void changeApplicationStatus() {
        if (applicationStatus == null)
            throw new IllegalStateException("청약 상태가 설정되지 않았습니다.");
    }

    // 청약 확정
    public void confirmApplication() {
        if (applicationId == null || applicationId.isEmpty())
            throw new IllegalStateException("청약번호가 없습니다.");
        this.applicationStatus = ApplicationStatus.APPROVED;
    }

    // 증권번호 발행
    public void issuePolicyNumber() {
        if (applicationStatus != ApplicationStatus.APPROVED)
            throw new IllegalStateException("승인된 청약만 증권번호를 발행할 수 있습니다.");
    }

    // 청약 접수 — 생성된 청약번호 반환
    public String receiveApplication() {
        this.applicationStatus = ApplicationStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
        this.applicationId = "APP-" + System.currentTimeMillis();
        return applicationId;
    }

    public String              getApplicationId()                       { return applicationId; }
    public void                setApplicationId(String v)              { this.applicationId = v; }
    public ApplicationStatus   getApplicationStatus()                  { return applicationStatus; }
    public void                setApplicationStatus(ApplicationStatus v){ this.applicationStatus = v; }
    public LocalDateTime       getAppliedAt()                          { return appliedAt; }
    public void                setAppliedAt(LocalDateTime v)           { this.appliedAt = v; }
    public String              getAppliedCondition()                   { return appliedCondition; }
    public void                setAppliedCondition(String v)           { this.appliedCondition = v; }
    public BigDecimal          getInsuredAmount()                      { return insuredAmount; }
    public void                setInsuredAmount(BigDecimal v)          { this.insuredAmount = v; }
    public String              getInsuredPersonInfo()                  { return insuredPersonInfo; }
    public void                setInsuredPersonInfo(String v)          { this.insuredPersonInfo = v; }
    public PaymentCycle        getPaymentCycle()                       { return paymentCycle; }
    public void                setPaymentCycle(PaymentCycle v)         { this.paymentCycle = v; }
    public BigDecimal          getPremium()                            { return premium; }
    public void                setPremium(BigDecimal v)                { this.premium = v; }
    public String              getProductCode()                        { return productCode; }
    public void                setProductCode(String v)                { this.productCode = v; }
    public SpecialContractType getSpecialContractList()                { return specialContractList; }
    public void                setSpecialContractList(SpecialContractType v){ this.specialContractList = v; }
    public String              getTermsVersion()                       { return termsVersion; }
    public void                setTermsVersion(String v)               { this.termsVersion = v; }

    @Override
    public String toString() {
        return "InsuranceApplication{applicationId='" + applicationId + "', applicationStatus=" + applicationStatus + "}";
    }
}
