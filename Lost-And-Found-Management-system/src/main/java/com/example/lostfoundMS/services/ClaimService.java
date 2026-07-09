package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.*;
import com.example.lostfoundMS.entities.dto.SubmitClaimRequest;
import com.example.lostfoundMS.entities.enums.ClaimStatus;
import com.example.lostfoundMS.entities.enums.CustodyStatus;
import com.example.lostfoundMS.entities.enums.ItemStatus;
import com.example.lostfoundMS.entities.enums.Role;
import com.example.lostfoundMS.repo.ClaimRepository;
import com.example.lostfoundMS.repo.ItemRepository;
import com.example.lostfoundMS.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;

    public ClaimService(ClaimRepository claimRepository, ItemRepository itemRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
    }

    public Claim submitClaim(SubmitClaimRequest request, User claimant) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (item.getUser() != null && item.getUser().getId().equals(claimant.getId())) {
            throw new IllegalArgumentException("You cannot claim an item you reported yourself");
        }

        if (claimRepository.existsByItemIdAndClaimantId(item.getId(), claimant.getId())) {
            throw new IllegalArgumentException("You've already submitted a claim on this item");
        }

        Claim claim = new Claim();
        claim.setItem(item);
        claim.setClaimant(claimant);
        claim.setProofDescription(request.getProofDescription());

        Claim saved = claimRepository.save(claim);

        item.setStatus(ItemStatus.MATCHED);
        itemRepository.save(item);

        return saved;
    }

    public Claim approveClaim(Long claimId, User reviewer) {
        Claim claim = getClaimAndCheckAuthority(claimId, reviewer);

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setReviewedBy(reviewer);
        Claim savedClaim = claimRepository.save(claim);

        Item item = claim.getItem();
        item.setStatus(ItemStatus.CLAIMED);
        if (item.getCustodyStatus() != null) {
            item.setCustodyStatus(CustodyStatus.RETURNED_TO_OWNER);
        }
        itemRepository.save(item);

        return savedClaim;
    }

    public Claim rejectClaim(Long claimId, User reviewer, String adminNote) {
        Claim claim = getClaimAndCheckAuthority(claimId, reviewer);

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setReviewedBy(reviewer);
        claim.setAdminNote(adminNote);
        Claim savedClaim = claimRepository.save(claim);

        Item item = claim.getItem();
        if (!claimRepository.findByItemId(item.getId()).stream()
                .anyMatch(c -> c.getStatus() == ClaimStatus.PENDING)) {
            item.setStatus(ItemStatus.ACTIVE);
            itemRepository.save(item);
        }

        return savedClaim;
    }

    private Claim getClaimAndCheckAuthority(Long claimId, User reviewer) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));

        boolean isFinder = claim.getItem().getUser() != null
                && claim.getItem().getUser().getId().equals(reviewer.getId());
        boolean isAdmin = reviewer.getRole() == Role.ADMIN;

        if (!isFinder && !isAdmin) {
            throw new SecurityException("You are not authorized to review this claim");
        }

        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new IllegalArgumentException("This claim has already been reviewed");
        }

        return claim;
    }

    public List<Claim> getClaimsForItem(Long itemId) {
        return claimRepository.findByItemId(itemId);
    }

    public List<Claim> getMyClaims(Long claimantId) {
        return claimRepository.findByClaimantId(claimantId);
    }
}