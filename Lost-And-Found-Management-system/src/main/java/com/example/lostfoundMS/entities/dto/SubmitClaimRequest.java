package com.example.lostfoundMS.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SubmitClaimRequest {
    @NotNull(message = "An item must be specified")
    private Long itemId;

    @NotBlank(message = "Please answer the verification question")
    @Size(min = 10, max = 1000, message = "Answer must be between 10 and 1000 characters")
    private String proofDescription;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getProofDescription() { return proofDescription; }
    public void setProofDescription(String proofDescription) { this.proofDescription = proofDescription; }
}
