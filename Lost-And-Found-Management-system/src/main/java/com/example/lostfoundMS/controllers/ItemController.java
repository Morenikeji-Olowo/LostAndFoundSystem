package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.services.ClaimService;
import com.example.lostfoundMS.services.ItemService;
import com.example.lostfoundMS.utils.AuthUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ClaimService claimService;

    @GetMapping("/browse")
    public String browseItems(@RequestParam(required = false) String keyword, HttpSession session, @RequestParam(required = false) String category, Model model){
        if(keyword != null && !keyword.isEmpty()){
            model.addAttribute("items", itemService.searchFoundItems(keyword));
            model.addAttribute("keyword",  keyword);
        }
        else if(category != null && !category.isEmpty()){
            model.addAttribute("items", itemService.getFoundItemsByCategory(category));
            model.addAttribute("category",  category);
        }
        else {
            model.addAttribute("items", itemService.getAllItems());
        }
        authUtils.addAuthAttributes(session,model);
        return "browse";
    }

    @GetMapping("/item/{id}")
    public String itemDetail(@PathVariable Long id, Model model, HttpSession session){

        Item item = itemService.getItemById(id);
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

        // debug AFTER everything is set
        System.out.println("=== ITEM TYPE: " + item.getType());
        System.out.println("=== ITEM STATUS: " + item.getItemStatus());
        System.out.println("=== IS OWNER: " + model.getAttribute("isOwner"));
        System.out.println("=== IS LOGGED IN: " + model.getAttribute("isLoggedIn"));

        return "item_detail";
    }
    @PostMapping("/claim/{itemId}")
    public String submitClaim(@PathVariable Long itemId,
                              @RequestParam String message, HttpSession session) {

        User curentUser = authUtils.getCurrentUser(session);
        claimService.createClaim(message, curentUser, itemId);

        return "redirect:/";
    }
}
