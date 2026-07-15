package com.example.lostfoundMS.repo;

import com.example.lostfoundMS.entities.Issue;
import com.example.lostfoundMS.entities.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByClaimId(Long claimId);

    // Admin's dashboard: everything still needing attention
    List<Issue> findByStatus(IssueStatus status);

    List<Issue> findByRaisedById(Long userId);
    long countByStatus(IssueStatus status);
}
