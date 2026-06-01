package claim.dto;

import jakarta.validation.constraints.NotBlank;

public class ObjectionCreateRequest {

    @NotBlank
    private String claimantName;

    @NotBlank
    private String claimantPhone;

    @NotBlank
    private String objectionReason;

    @NotBlank
    private String requestedAction;

    @NotBlank
    private String employeeNo;

    public String getClaimantName() { return claimantName; }

    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimantPhone() { return claimantPhone; }

    public void setClaimantPhone(String claimantPhone) { this.claimantPhone = claimantPhone; }

    public String getObjectionReason() { return objectionReason; }

    public void setObjectionReason(String objectionReason) { this.objectionReason = objectionReason; }

    public String getRequestedAction() { return requestedAction; }

    public void setRequestedAction(String requestedAction) { this.requestedAction = requestedAction; }

    public String getEmployeeNo() { return employeeNo; }

    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
}
