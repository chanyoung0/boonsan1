package model.contract;

import enums.ApplicationStatus;
import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

// 보험 계약 도메인 모델 — 증권발행 완료 후 생성되는 핵심 계약 엔티티
public class Contract {

    private String policyNo;
    private String applicationId;
    private String insuredPersonName;
    private String productCode;
    private ApplicationStatus status;
    private BigDecimal insuredAmount;
    private BigDecimal premium;
    private PaymentMethod paymentMethod;
    private String paymentCycle;
    private LocalDate contractDate;
    private LocalDate coverageStartDate;
    private LocalDate coverageEndDate;
    private String appliedCondition;

    // 계약 기본정보로 초기화
    public Contract(String policyNo, String applicationId, String insuredPersonName,
                    String productCode, BigDecimal insuredAmount, BigDecimal premium,
                    LocalDate coverageStartDate, LocalDate coverageEndDate, String appliedCondition) {
        this.policyNo = policyNo;
        this.applicationId = applicationId;
        this.insuredPersonName = insuredPersonName;
        this.productCode = productCode;
        this.insuredAmount = insuredAmount;
        this.premium = premium;
        this.coverageStartDate = coverageStartDate;
        this.coverageEndDate = coverageEndDate;
        this.appliedCondition = appliedCondition;
        this.status = ApplicationStatus.APPROVED;
        this.contractDate = LocalDate.now();
    }

    // 계약 상태 변경
    public void changeContractStatus() {}

    // 납입 상태 확인
    public void checkPaymentStatus() {}

    // 계약 실행
    public void executeContract() {}

    // 계약 정보 조회
    public void getContractInfo() {}

    // 증권번호 발행
    public void issuePolicyNumber() {}

    // 계약 갱신
    public void renewContract() {}

    // 계약 종료
    public void terminateContract() {}

    public String getPolicyNo()           { return policyNo; }
    public String getApplicationId()      { return applicationId; }
    public String getInsuredPersonName()  { return insuredPersonName; }
    public String getProductCode()        { return productCode; }
    public ApplicationStatus getStatus()  { return status; }
    public BigDecimal getInsuredAmount()  { return insuredAmount; }
    public BigDecimal getPremium()        { return premium; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getPaymentCycle()       { return paymentCycle; }
    public LocalDate getContractDate()    { return contractDate; }
    public LocalDate getCoverageStartDate() { return coverageStartDate; }
    public LocalDate getCoverageEndDate() { return coverageEndDate; }
    public String getAppliedCondition()   { return appliedCondition; }

    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentCycle(String paymentCycle)          { this.paymentCycle = paymentCycle; }

    @Override
    public String toString() {
        return "Contract{policyNo='" + policyNo + "', insuredPerson='" + insuredPersonName
                + "', status=" + status + ", coverage=" + coverageStartDate + "~" + coverageEndDate + "}";
    }
}
