package underwriting.dto;

public class UnderwritingDeductionItemResponse {

    private String itemName;
    private String itemValue;
    private int deduction;
    private String reason;

    public UnderwritingDeductionItemResponse() {
    }

    public UnderwritingDeductionItemResponse(String itemName, String itemValue, int deduction, String reason) {
        this.itemName = itemName;
        this.itemValue = itemValue;
        this.deduction = deduction;
        this.reason = reason;
    }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemValue() { return itemValue; }
    public void setItemValue(String itemValue) { this.itemValue = itemValue; }
    public int getDeduction() { return deduction; }
    public void setDeduction(int deduction) { this.deduction = deduction; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
