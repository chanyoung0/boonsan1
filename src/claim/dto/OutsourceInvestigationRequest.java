package claim.dto;

import jakarta.validation.constraints.NotBlank;

public class OutsourceInvestigationRequest {
    @NotBlank
    private String employeeNo;
    @NotBlank
    private String partnerName;
    @NotBlank
    private String materialChecklist;
    @NotBlank
    private String requestDetails;

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public String getMaterialChecklist() { return materialChecklist; }
    public void setMaterialChecklist(String materialChecklist) { this.materialChecklist = materialChecklist; }
    public String getRequestDetails() { return requestDetails; }
    public void setRequestDetails(String requestDetails) { this.requestDetails = requestDetails; }
}
