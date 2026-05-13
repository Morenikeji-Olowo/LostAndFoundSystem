package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.*;
import com.example.lostfoundMS.services.ClaimService;
import com.example.lostfoundMS.services.ItemService;
import com.example.lostfoundMS.services.UserService;
import com.example.lostfoundMS.utils.AuthUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
    public class ClaimController {
        @Autowired ClaimService claimService;
        @Autowired AuthUtils authUtils;
        @Autowired ItemService itemService;

    @PostMapping("/claim/{itemId}")
    public String submitClaim(@PathVariable Long itemId,
                              @RequestParam String message, HttpSession session, Model model) {

        User curentUser = authUtils.getCurrentUser(session);
        claimService.createClaim(message, curentUser, itemId);
        Item item = itemService.getItemById(itemId);
        model.addAttribute("item", item);
        authUtils.addAuthAttributes(session, model);

        if(authUtils.isLoggedIn(session)) {
            User currentUser = authUtils.getCurrentUser(session);
            boolean owner = item.getUser() != null &&
                    item.getUser().getId().equals(currentUser.getId());
            model.addAttribute("isOwner", owner);
        } else {
            model.addAttribute("isOwner", false);
        }
        model.addAttribute("claims", claimService.getClaimsByItemId(item.getId()));
        return "item_detail";
    }

    @GetMapping("/item/{id}/claims")
        public String itemClaims(@PathVariable Long id, Model model, HttpSession session) {
            Item item = itemService.getItemById(id);
            authUtils.addAuthAttributes(session, model);

            User currentUser = authUtils.getCurrentUser(session);
            if (!item.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/item/" + id; // not the owner, send them away
            }

            model.addAttribute("item", item);
            model.addAttribute("claims", claimService.getClaimsByItemId(id));
            return "item_claims";
        }

        @PostMapping("/claim/{id}/approve")
        public String approveClaim(@PathVariable Long id, HttpSession session) {
            Claim claim = claimService.getClaimById(id);
            claim.setStatus(ClaimStatus.APPROVED);
            claimService.save(claim);

            // mark the item as claimed
            Item item = claim.getItem();
            item.setStatus(ItemStatus.CLAIMED);
            itemService.save(item);

            return "redirect:/item/" + item.getId() + "/claims";
        }

        @PostMapping("/claim/{id}/reject")
        public String rejectClaim(@PathVariable Long id, HttpSession session) {
            Claim claim = claimService.getClaimById(id);
            claim.setStatus(ClaimStatus.REJECTED);
            claimService.save(claim);

            return "redirect:/item/" + claim.getItem().getId() + "/claims";
        }
    }

