package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.Item;
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

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ItemService itemService;
    private final MatchService matchService;
    private final IssueService issueService;

    public AdminController(ItemService itemService, MatchService matchService, IssueService issueService) {
        this.itemService = itemService;
        this.matchService = matchService;
        this.issueService = issueService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pendingItemsCount", itemService.getPendingModeration().size());
        model.addAttribute("openIssuesCount", issueService.getOpenIssues().size());
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