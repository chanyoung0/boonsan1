package claim.dto;

import java.time.LocalDateTime;

public class ClaimAlternativeFlowResponse {
    private String actionId;
    private String accidentNumber;
    private String actionType;
    private String employeeNo;
    private String reason;
    private String partnerName;
    private String materialChecklist;
    private String resultMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public String getActionId() { return actionId; }
    public void setActionId(String actionId) { this.actionId = actionId; }
    public String getAccidentNumber() { return accidentNumber; }
    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public String getMaterialChecklist() { return materialChecklist; }
    public void setMaterialChecklist(String materialChecklist) { this.materialChecklist = materialChecklist; }
    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
