package contract.dto;

import java.util.List;

public class PaymentCollectionBatchRequest {

    private List<String> policyNumbers;

    public List<String> getPolicyNumbers() { return policyNumbers; }
    public void setPolicyNumbers(List<String> policyNumbers) { this.policyNumbers = policyNumbers; }
}
