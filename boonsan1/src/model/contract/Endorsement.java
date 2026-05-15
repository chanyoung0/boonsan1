package model.contract;

import enums.ChangeReason;
import enums.EndorsementType;

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
