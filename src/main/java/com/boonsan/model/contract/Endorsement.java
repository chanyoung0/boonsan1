package com.boonsan.model.contract;

import com.boonsan.enums.ChangeReason;
import com.boonsan.enums.EndorsementType;

import java.time.LocalDateTime;

public class Endorsement {

    private LocalDateTime appliedAt;
    private ChangeReason changeReason;
    private EndorsementType endorsementType;
    private String newContent;
    private String previousContent;
    private LocalDateTime processedAt;

    public void applyEndorsement() {}

    public void processEndorsement() {}

    public void verifyChanges() {}
}
