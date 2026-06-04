package com.boonsan.contract.dto;

import com.boonsan.enums.ChangeReason;
import com.boonsan.enums.EndorsementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EndorsementCreateRequest {

    @NotNull
    private EndorsementType endorsementType;

    @NotNull
    private ChangeReason changeReason;

    @NotBlank
    @Size(max = 2000)
    private String previousContent;

    @NotBlank
    @Size(max = 2000)
    private String newContent;

    public EndorsementType getEndorsementType() { return endorsementType; }
    public void setEndorsementType(EndorsementType endorsementType) { this.endorsementType = endorsementType; }

    public ChangeReason getChangeReason() { return changeReason; }
    public void setChangeReason(ChangeReason changeReason) { this.changeReason = changeReason; }

    public String getPreviousContent() { return previousContent; }
    public void setPreviousContent(String previousContent) { this.previousContent = previousContent; }

    public String getNewContent() { return newContent; }
    public void setNewContent(String newContent) { this.newContent = newContent; }
}
