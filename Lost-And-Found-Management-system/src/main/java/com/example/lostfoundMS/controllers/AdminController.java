package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.enums.*;
import com.example.lostfoundMS.services.ClaimService;
import com.example.lostfoundMS.services.IssueService;
import com.example.lostfoundMS.services.ItemService;
import com.example.lostfoundMS.services.MatchService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ItemService itemService;
    private final MatchService matchService;
    private final IssueService issueService;
    private final ClaimService claimService;

    public AdminController(ItemService itemService, MatchService matchService, IssueService issueService, ClaimService claimService) {
        this.itemService = itemService;
        this.matchService = matchService;
        this.issueService = issueService;
        this.claimService = claimService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> stats = new HashMap<>();

        long totalItems = itemService.countAll();
        long resolvedItems = itemService.countByStatus(ItemStatus.RESOLVED);
        double successRate = totalItems > 0 ? (double) resolvedItems / totalItems * 100 : 0;

        stats.put("totalItems", totalItems);
        stats.put("lostItems", itemService.countByType(ItemType.LOST));
        stats.put("foundItems", itemService.countByType(ItemType.FOUND));
        stats.put("pendingModeration", itemService.countByModerationStatus(ItemModerationStatus.PENDING));
        stats.put("activeClaims", claimService.countActive());
        stats.put("pendingClaims", claimService.countByStatus(ClaimStatus.PENDING));
        stats.put("openIssues", issueService.countByStatus(IssueStatus.OPEN));
        stats.put("totalClaims", claimService.countAll());
        stats.put("resolvedItems", resolvedItems);
        stats.put("successRate", Math.round(successRate));

        model.addAttribute("stats", stats);
        model.addAttribute("recentItems", itemService.findRecent(10));

        return "admin-dashboard";
    }

    @GetMapping("/admin/moderation")
    public String moderationQueue(Model model) {
        model.addAttribute("items", itemService.getPendingModeration());
        return "admin-moderation";
    }

    @PostMapping("/admin/moderation/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Item approved = itemService.approveItem(id);
        matchService.findMatchesFor(approved);
        redirectAttributes.addFlashAttribute("successMessage", "Item approved and matched.");
        return "redirect:/admin/moderation";
    }

    @PostMapping("/admin/moderation/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes redirectAttributes) throws IOException {
        itemService.rejectItem(id);
        redirectAttributes.addFlashAttribute("successMessage", "Item rejected.");
        return "redirect:/admin/moderation";
    }
}