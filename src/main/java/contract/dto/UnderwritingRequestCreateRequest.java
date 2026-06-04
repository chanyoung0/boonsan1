package contract.dto;

import enums.UnderwritingType;

public class UnderwritingRequestCreateRequest {

    private UnderwritingType underwritingType;

    public UnderwritingType getUnderwritingType() { return underwritingType; }
    public void setUnderwritingType(UnderwritingType underwritingType) {
        this.underwritingType = underwritingType;
    }
}
