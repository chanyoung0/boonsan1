package claim.dto;

import jakarta.validation.constraints.NotBlank;

public class DamageInvestigationRejectRequest {
    @NotBlank
    private String employeeNo;
    @NotBlank
    private String rejectionReason;

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
