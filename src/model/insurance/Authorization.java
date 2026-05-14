package model.insurance;

import java.time.LocalDateTime;

public class Authorization {

    private LocalDateTime approveAt;
    private boolean isApproved;
    private String location;
    private String requestId;
    private String requestReason;
    private String submissionAgencyName;

    public void applyAuthorizationResult() {}

    public void cancelAuthorizationRequest() {}

    public void sendAuthorizationRequest() {}

    public void updateProductStatus() {}
}
