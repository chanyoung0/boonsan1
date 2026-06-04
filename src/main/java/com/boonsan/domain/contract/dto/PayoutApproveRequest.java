package com.boonsan.domain.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PayoutApproveRequest {

    @NotBlank
    @Size(max = 50)
    private String processor;

    public String getProcessor() { return processor; }

    public void setProcessor(String processor) { this.processor = processor; }
}
