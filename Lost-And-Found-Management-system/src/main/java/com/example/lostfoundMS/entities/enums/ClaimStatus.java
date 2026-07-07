package com.example.lostfoundMS.entities.enums;

public enum ClaimStatus {
    PENDING,    // awaiting finder/admin review
    APPROVED,   // verified, contact info released between parties
    REJECTED    // verification failed, item stays open for other claimants
}
