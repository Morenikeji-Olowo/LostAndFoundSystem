package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.entities.dto.ReportItemRequest;
import com.example.lostfoundMS.services.ClaimService;
import com.example.lostfoundMS.services.ItemService;
import com.example.lostfoundMS.services.MatchService;
import com.example.lostfoundMS.utils.AuthUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;


@Controller
public class ItemController {
    private final ItemService itemService;
    private final MatchService matchService;

    public ItemController(ItemService itemService, MatchService matchService) {
        this.itemService = itemService;
        this.matchService = matchService;
    }

    // Public bulletin board
    @GetMapping("/items")
    public String board(Model model) {
        List<Item> items = itemService.getPublicBoard();
        model.addAttribute("items", items);
        return "items";
    }

    @GetMapping("/items/report")
    public String showReportForm(Model model) {
        model.addAttribute("reportItemRequest", new ReportItemRequest());
        return "report-item";
    }

    @PostMapping("/items/report")
    public String submitReport(
            @Valid @ModelAttribute("reportItemRequest") ReportItemRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "report-item";
        }

        try {
            itemService.reportItem(request, currentUser);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("error", e.getMessage());
            return "report-item";
        } catch (IOException e) {
            bindingResult.reject("error", "Photo upload failed. Please try again.");
            return "report-item";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Item reported successfully.");
        return "redirect:/items";
    }

    @GetMapping("/my-items")
    public String myItems(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("items", itemService.getUserItems(currentUser.getId()));
        return "my-items";
    }
}
