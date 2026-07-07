package com.example.lostfoundMS.repo;

import com.example.lostfoundMS.entities.Claim;
import com.example.lostfoundMS.entities.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // All claims on a specific item — the finder needs to see these
    List<Claim> findByItemId(Long itemId);

    // A claimant's own claims, for their dashboard
    List<Claim> findByClaimantId(Long claimantId);

    // Claims still awaiting a decision — used for the finder's/admin's action queue
    List<Claim> findByStatus(ClaimStatus status);

    // Guard against duplicate claims: same person claiming the same item twice
    boolean existsByItemIdAndClaimantId(Long itemId, Long claimantId);
}
