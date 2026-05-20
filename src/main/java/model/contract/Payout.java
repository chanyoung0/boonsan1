package model.contract;

import enums.CalculationBasis;
import enums.PaymentType;
import model.accident.DamageInvestigation;
import model.accident.InsurancePayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 지급 결정 도메인 모델 — 보험금 지급 승인 및 산정 정보 관리
public class Payout {

    private String payoutId;
    private String policyNumber;
    private LocalDateTime approvedAt;
    private BigDecimal calculatedAmount;
    private CalculationBasis calculationBasis;
    private String deductionItem;
    private BigDecimal finalPaymentAmount;
    private LocalDateTime paidAt;
    private PaymentType paymentType;
    private String processor;
    private String payoutStatus;

    private DamageInvestigation damageInvestigation;
    private InsurancePayment insurancePayment;

    public Payout() {}

    public Payout(String processor, PaymentType paymentType, CalculationBasis calculationBasis,
                  BigDecimal calculatedAmount, BigDecimal finalPaymentAmount,
                  String deductionItem, LocalDateTime approvedAt) {
        this.processor = processor;
        this.paymentType = paymentType;
        this.calculationBasis = calculationBasis;
        this.calculatedAmount = calculatedAmount;
        this.finalPaymentAmount = finalPaymentAmount;
        this.deductionItem = deductionItem;
        this.approvedAt = approvedAt;
    }

    public void approvePayment()  {
        this.approvedAt = LocalDateTime.now();
    }

    public BigDecimal calculatePayment(){
        if (this.finalPaymentAmount == null) {
            this.finalPaymentAmount = this.calculatedAmount;
        }
        return this.finalPaymentAmount;
    }

    public void cancelPayment()   {
        // 취소 상태 필드가 없어 취소 여부는 service 목록에서 관리한다.
    }

    public void processPayment()  {
        this.paidAt = LocalDateTime.now();
    }

    public String              getPayoutId()                            { return payoutId; }
    public void                setPayoutId(String v)                   { this.payoutId = v; }
    public String              getPolicyNumber()                        { return policyNumber; }
    public void                setPolicyNumber(String v)               { this.policyNumber = v; }
    public LocalDateTime       getApprovedAt()                          { return approvedAt; }
    public void                setApprovedAt(LocalDateTime v)           { this.approvedAt = v; }
    public BigDecimal          getCalculatedAmount()                    { return calculatedAmount; }
    public void                setCalculatedAmount(BigDecimal v)        { this.calculatedAmount = v; }
    public CalculationBasis    getCalculationBasis()                    { return calculationBasis; }
    public void                setCalculationBasis(CalculationBasis v)  { this.calculationBasis = v; }
    public String              getDeductionItem()                       { return deductionItem; }
    public void                setDeductionItem(String v)               { this.deductionItem = v; }
    public BigDecimal          getFinalPaymentAmount()                  { return finalPaymentAmount; }
    public void                setFinalPaymentAmount(BigDecimal v)      { this.finalPaymentAmount = v; }
    public LocalDateTime       getPaidAt()                              { return paidAt; }
    public void                setPaidAt(LocalDateTime v)               { this.paidAt = v; }
    public PaymentType         getPaymentType()                         { return paymentType; }
    public void                setPaymentType(PaymentType v)            { this.paymentType = v; }
    public String              getProcessor()                           { return processor; }
    public void                setProcessor(String v)                   { this.processor = v; }
    public String              getPayoutStatus()                        { return payoutStatus; }
    public void                setPayoutStatus(String v)                { this.payoutStatus = v; }
    public DamageInvestigation getDamageInvestigation()                 { return damageInvestigation; }
    public void                setDamageInvestigation(DamageInvestigation v){ this.damageInvestigation = v; }
    public InsurancePayment    getInsurancePayment()                    { return insurancePayment; }
    public void                setInsurancePayment(InsurancePayment v)  { this.insurancePayment = v; }

    @Override
    public String toString() {
        return "Payout{processor='" + processor + "', finalPaymentAmount=" + finalPaymentAmount + "}";
    }
}
