package product.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthorizationRevisionRequest {

    @NotBlank
    private String revisionRequest;

    public String getRevisionRequest() { return revisionRequest; }
    public void setRevisionRequest(String revisionRequest) { this.revisionRequest = revisionRequest; }
}
