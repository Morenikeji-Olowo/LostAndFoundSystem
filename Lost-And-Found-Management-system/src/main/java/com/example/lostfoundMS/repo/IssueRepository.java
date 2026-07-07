package com.example.lostfoundMS.repo;

import com.example.lostfoundMS.entities.Issue;
import com.example.lostfoundMS.entities.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByClaimId(Long claimId);

    // Admin's dashboard: everything still needing attention
    List<Issue> findByStatus(IssueStatus status);

    List<Issue> findByRaisedById(Long userId);
}
