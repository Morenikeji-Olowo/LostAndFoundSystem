package com.example.lostfoundMS.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RaiseIssueRequest {
    @NotNull(message = "A claim must be specified")
    private Long claimId;

    @NotBlank(message = "Please describe the issue")
    @Size(min = 10, max = 1000, message = "Message must be between 10 and 1000 characters")
    private String message;

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }


}
