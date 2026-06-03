package contract.dto;

import java.math.BigDecimal;

public class PaymentCollectionTransferTargetResponse {

    private String policyNumber;
    private String insuredName;
    private Integer unpaidInstallmentCount;
    private BigDecimal unpaidAmount;

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getInsuredName() { return insuredName; }
    public void setInsuredName(String insuredName) { this.insuredName = insuredName; }

    public Integer getUnpaidInstallmentCount() { return unpaidInstallmentCount; }
    public void setUnpaidInstallmentCount(Integer unpaidInstallmentCount) {
        this.unpaidInstallmentCount = unpaidInstallmentCount;
    }

    public BigDecimal getUnpaidAmount() { return unpaidAmount; }
    public void setUnpaidAmount(BigDecimal unpaidAmount) { this.unpaidAmount = unpaidAmount; }
}
