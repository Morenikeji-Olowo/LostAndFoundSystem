package com.example.lostfoundMS.entities.enums;

public enum ItemStatus {
    ACTIVE,     // visible on the public bulletin board
    MATCHED,    // a candidate match exists, awaiting claim
    CLAIMED,    // a claim has been approved, pending physical handover
    RESOLVED,   // item returned to owner, done
    ARCHIVED    // unclaimed past the auto-archive window
}
