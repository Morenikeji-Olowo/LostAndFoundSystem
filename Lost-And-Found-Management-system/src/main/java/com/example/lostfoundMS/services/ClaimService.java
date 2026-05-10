package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.*;
import com.example.lostfoundMS.repo.ClaimRepository;
import com.example.lostfoundMS.repo.ItemRepository;
import com.example.lostfoundMS.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;


    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public Claim createClaim(String message, User user, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()->new RuntimeException("Item not found")
        );
        Claim claim = new Claim();
        claim.setUser(user);
        claim.setItem(item);
        claim.setProofDescription(message);
        return claimRepository.save(claim);
    }

    public List<Claim> getClaimsByItemId(Long itemId) {
        return claimRepository.findByItemId(itemId);
    }

    public Claim updateStatus(Long claimId, ClaimStatus status) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus(status);
        return claimRepository.save(claim);
    }

    public List<Claim> getClaimsByUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));
        return claimRepository.findClaimByUserId(user.getId());
    }
}