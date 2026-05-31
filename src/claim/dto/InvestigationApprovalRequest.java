package claim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InvestigationApprovalRequest {

    @NotBlank
    @Size(max = 50)
    private String accidentNumber;

    @NotBlank
    @Size(max = 50)
    private String employeeNo;

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getEmployeeNo() { return employeeNo; }

    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
}
