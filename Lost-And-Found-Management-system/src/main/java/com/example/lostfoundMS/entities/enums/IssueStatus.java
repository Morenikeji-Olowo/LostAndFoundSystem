package com.example.lostfoundMS.entities.enums;

public enum IssueStatus {
    OPEN,           // just raised, needs admin attention
    IN_PROGRESS,    // admin is actively looking into it
    RESOLVED        // closed out, adminResponse should be filled in
}
