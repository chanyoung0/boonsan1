package contract.dto;

import jakarta.validation.constraints.NotNull;

public class MaturityRenewalIntentionRequest {

    @NotNull
    private Boolean renewalIntention;

    public Boolean getRenewalIntention() { return renewalIntention; }
    public void setRenewalIntention(Boolean renewalIntention) { this.renewalIntention = renewalIntention; }
}
