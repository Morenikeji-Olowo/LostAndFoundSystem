package com.example.lostfoundMS.entities;

import com.example.lostfoundMS.entities.enums.IssueStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "An issue must reference a claim")
    @ManyToOne
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @NotNull(message = "An issue must have a reporter")
    @ManyToOne
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @NotBlank(message = "Please describe the issue")
    @Size(min = 10, max = 1000, message = "Message must be between 10 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String message;

    @NotNull(message = "Issue status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;

    @ManyToOne
    private User handledBy;

    @Size(max = 1000, message = "Response must be under 1000 characters")
    @Column(length = 1000)
    private String adminResponse;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    public Issue() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = IssueStatus.OPEN;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public User getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(User raisedBy) {
        this.raisedBy = raisedBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public User getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(User handledBy) {
        this.handledBy = handledBy;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
