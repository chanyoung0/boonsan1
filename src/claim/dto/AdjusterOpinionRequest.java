package claim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdjusterOpinionRequest {

    @NotBlank
    @Size(max = 50)
    private String accidentNumber;

    @NotBlank
    private String faultRatioOpinion;

    @NotBlank
    private String adjusterOpinion;

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getFaultRatioOpinion() { return faultRatioOpinion; }

    public void setFaultRatioOpinion(String faultRatioOpinion) { this.faultRatioOpinion = faultRatioOpinion; }

    public String getAdjusterOpinion() { return adjusterOpinion; }

    public void setAdjusterOpinion(String adjusterOpinion) { this.adjusterOpinion = adjusterOpinion; }
}
