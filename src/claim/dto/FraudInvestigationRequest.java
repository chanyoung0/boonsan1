package claim.dto;

import jakarta.validation.constraints.NotBlank;

public class FraudInvestigationRequest {
    @NotBlank
    private String employeeNo;
    @NotBlank
    private String confirmation;

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getConfirmation() { return confirmation; }
    public void setConfirmation(String confirmation) { this.confirmation = confirmation; }
}
