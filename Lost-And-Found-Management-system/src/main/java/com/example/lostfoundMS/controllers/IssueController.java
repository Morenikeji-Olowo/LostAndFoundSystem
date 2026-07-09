package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.entities.dto.RaiseIssueRequest;
import com.example.lostfoundMS.services.IssueService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IssueController {
    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/issues/raise")
    public String showRaiseForm(@RequestParam Long claimId, Model model) {
        RaiseIssueRequest request = new RaiseIssueRequest();
        request.setClaimId(claimId);
        model.addAttribute("raiseIssueRequest", request);
        return "raise-issue";
    }

    @PostMapping("/issues/raise")
    public String raiseIssue(
            @Valid @ModelAttribute("raiseIssueRequest") RaiseIssueRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "raise-issue";
        }

        try {
            issueService.raiseIssue(request, currentUser);
        } catch (SecurityException e) {
            bindingResult.reject("error", "You're not involved in this claim.");
            return "raise-issue";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("error", e.getMessage());
            return "raise-issue";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Issue reported. Admin will review it.");
        return "redirect:/my-items";
    }

    //Admin

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/issues")
    public String openIssues(Model model) {
        model.addAttribute("issues", issueService.getOpenIssues());
        return "admin-issues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/issues/{id}/start")
    public String startProgress(
            @PathVariable Long id, @AuthenticationPrincipal User admin,
            RedirectAttributes redirectAttributes
    ) {
        issueService.startProgress(id, admin);
        redirectAttributes.addFlashAttribute("successMessage", "Marked as in progress.");
        return "redirect:/admin/issues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/issues/{id}/resolve")
    public String resolve(
            @PathVariable Long id, @RequestParam String adminResponse,
            @AuthenticationPrincipal User admin, RedirectAttributes redirectAttributes
    ) {
        issueService.resolveIssue(id, admin, adminResponse);
        redirectAttributes.addFlashAttribute("successMessage", "Issue resolved.");
        return "redirect:/admin/issues";
    }
}
