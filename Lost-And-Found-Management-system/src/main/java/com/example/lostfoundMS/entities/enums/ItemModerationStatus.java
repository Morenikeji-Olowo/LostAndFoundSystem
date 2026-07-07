package com.example.lostfoundMS.entities.enums;

public enum ItemModerationStatus {
    PENDING,    // awaiting admin review before going public
    APPROVED,   // passed moderation, visible on the bulletin board
    REJECTED    // spam/inappropriate, hidden from public view
}
