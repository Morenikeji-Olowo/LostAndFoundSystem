package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.ClaimStatus;
import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.ItemType;
import com.example.lostfoundMS.services.ClaimService;
import com.example.lostfoundMS.services.ItemService;
import com.example.lostfoundMS.services.UserService;
import com.example.lostfoundMS.utils.AuthUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ClaimService claimService;

    @GetMapping("/")
    public String homePage(HttpSession session, Model model) {
        authUtils.addAuthAttributes(session, model);
        model.addAttribute("items", itemService.getTop5Items());
        return "index";
    }
    @GetMapping("/report")
    public String showReport(Model model, HttpSession session) {
        model.addAttribute("item", new Item());
        authUtils.addAuthAttributes(session, model);
        return "report";
    }
    @GetMapping("/dashBoard")
    public String showDashBoard(Model model,  HttpSession session) {
        authUtils.addAuthAttributes(session, model);
        String email = authUtils.getCurrentUser(session).getEmail();
        var reports = itemService.getItemsByUser(email);
        var claims = claimService.getClaimsByUser(email);
        model.addAttribute("reports", reports);

        long lostCount = reports.stream()
                .filter(i->i.getType() == ItemType.LOST)
                .count();
        long foundCount = reports.stream()
                .filter(i->i.getType() == ItemType.FOUND)
                .count();
        long claimCount = claims.size();

        model.addAttribute("claimsCount", claimCount);

        model.addAttribute("lostCount", lostCount);
        model.addAttribute("foundCount", foundCount);

        //empty array for now
        model.addAttribute("claims", claims);
        return "dashboard";
    }

    @PostMapping("/report")
    public String postReport(@ModelAttribute Item item, @RequestParam String type, @RequestParam(required = false) MultipartFile photo, Model model,  HttpSession session) {
        try{
            String email = authUtils.getCurrentUser(session).getEmail();
            if (type.equals("LOST")) {
                itemService.reportLostItem(item, email, photo);
            } else {
                itemService.reportFoundItem(item, email, photo);
            }

            return "redirect:/dashBoard";
        }
        catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();  // this prints the full error to console
            model.addAttribute("error", e.getMessage());
            model.addAttribute("item", item);
            authUtils.addAuthAttributes(session, model);
            return "report";
        }
    }
}
