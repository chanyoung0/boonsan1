package model.contract;

import enums.ApplicationStatus;
import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

// 보험 계약 도메인 모델 — 증권발행 완료 후 생성되는 핵심 계약 엔티티
public class Contract {

    private String policyNo;            // 증권번호
    private String applicationId;       // 청약번호
    private String insuredPersonName;   // 피보험자명
    private String productCode;         // 상품코드
    private ApplicationStatus status;   // 계약 상태
    private BigDecimal insuredAmount;   // 보험가입금액
    private BigDecimal premium;         // 보험료
    private PaymentMethod paymentMethod; // 납입방법
    private String paymentCycle;        // 납입주기
    private LocalDate contractDate;     // 계약일
    private LocalDate coverageStartDate; // 보장 개시일
    private LocalDate coverageEndDate;   // 보장 만료일
    private String appliedCondition;    // 적용조건 (표준체/할증체)

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
    public void changeStatus(ApplicationStatus newStatus) {}

    // 계약 정보 저장
    public void save() {}

    // 계약 만기 여부 확인
    public boolean isExpired() {
        return LocalDate.now().isAfter(coverageEndDate);
    }

    // 계약 유효 여부 확인
    public boolean isActive() {
        return status == ApplicationStatus.APPROVED;
    }

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
