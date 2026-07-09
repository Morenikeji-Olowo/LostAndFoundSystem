package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.Claim;
import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.entities.dto.SubmitClaimRequest;
import com.example.lostfoundMS.services.ClaimService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClaimController {
    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/claims/submit")
    public String showClaimForm(@RequestParam Long itemId, Model model) {
        SubmitClaimRequest request = new SubmitClaimRequest();
        request.setItemId(itemId);
        model.addAttribute("submitClaimRequest", request);
        return "submit-claim";
    }

    @PostMapping("/claims/submit")
    public String submitClaim(
            @Valid @ModelAttribute("submitClaimRequest") SubmitClaimRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "submit-claim";
        }

        try {
            claimService.submitClaim(request, currentUser);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("error", e.getMessage());
            return "submit-claim";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Claim submitted. The reporter will review it."
        );
        return "redirect:/my-items";
    }

    // Claims on a specific item - the finder's review screen
    @GetMapping("/items/{itemId}/claims")
    public String claimsForItem(@PathVariable Long itemId, Model model) {
        model.addAttribute("claims", claimService.getClaimsForItem(itemId));
        return "item-claims";
    }

    @PostMapping("/claims/{id}/approve")
    public String approve(
            @PathVariable Long id, @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Claim claim = claimService.approveClaim(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Claim approved.");
            return "redirect:/items/" + claim.getItem().getId() + "/claims";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "You're not authorized to do that.");
            return "redirect:/my-items";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/my-items";
        }
    }

    @PostMapping("/claims/{id}/reject")
    public String reject(
            @PathVariable Long id, @RequestParam(required = false) String adminNote,
            @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes
    ) {
        try {
            Claim claim = claimService.rejectClaim(id, currentUser, adminNote);
            redirectAttributes.addFlashAttribute("successMessage", "Claim rejected.");
            return "redirect:/items/" + claim.getItem().getId() + "/claims";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "You're not authorized to do that.");
            return "redirect:/my-items";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/my-items";
        }
    }
}
