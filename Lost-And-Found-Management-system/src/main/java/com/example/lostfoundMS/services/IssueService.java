package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.Claim;
import com.example.lostfoundMS.entities.Issue;
import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.entities.dto.RaiseIssueRequest;
import com.example.lostfoundMS.entities.enums.IssueStatus;
import com.example.lostfoundMS.entities.enums.Role;
import com.example.lostfoundMS.repo.ClaimRepository;
import com.example.lostfoundMS.repo.IssueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final ClaimRepository claimRepository;

    public IssueService(IssueRepository issueRepository, ClaimRepository claimRepository) {
        this.issueRepository = issueRepository;
        this.claimRepository = claimRepository;
    }

    public Issue raiseIssue(RaiseIssueRequest request, User raisedBy) {
        Claim claim = claimRepository.findById(request.getClaimId())
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));

        boolean isClaimant = claim.getClaimant().getId().equals(raisedBy.getId());
        boolean isFinder = claim.getItem().getUser() != null
                && claim.getItem().getUser().getId().equals(raisedBy.getId());

        if (!isClaimant && !isFinder) {
            throw new SecurityException("You are not involved in this claim");
        }

        Issue issue = new Issue();
        issue.setClaim(claim);
        issue.setRaisedBy(raisedBy);
        issue.setMessage(request.getMessage());

        return issueRepository.save(issue);
    }

    public Issue startProgress(Long issueId, User admin) {
        Issue issue = getIssueAndCheckAdmin(issueId, admin);
        issue.setStatus(IssueStatus.IN_PROGRESS);
        issue.setHandledBy(admin);
        return issueRepository.save(issue);
    }

    public Issue resolveIssue(Long issueId, User admin, String adminResponse) {
        Issue issue = getIssueAndCheckAdmin(issueId, admin);

        issue.setStatus(IssueStatus.RESOLVED);
        issue.setHandledBy(admin);
        issue.setAdminResponse(adminResponse);
        issue.setResolvedAt(LocalDateTime.now());

        return issueRepository.save(issue);
    }

    private Issue getIssueAndCheckAdmin(Long issueId, User admin) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new SecurityException("Only admins can act on issues");
        }

        return issue;
    }

    public List<Issue> getOpenIssues() {
        return issueRepository.findByStatus(IssueStatus.OPEN);
    }

    public List<Issue> getIssuesForClaim(Long claimId) {
        return issueRepository.findByClaimId(claimId);
    }
}
