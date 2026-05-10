package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        try {
            User user = userService.loginUser(email, password);
            System.out.println("=== LOGIN SUCCESS: " + user.getEmail());
            session.setAttribute("loggedInUser", user);
            return "redirect:/";

        } catch (RuntimeException e) {
            System.out.println("=== LOGIN FAILED: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute User user, Model model){
        try{
            if(!user.getPassword().equals(user.getConfirmPassword())){
                model.addAttribute("error","Passwords do not match");
                return "register";
            }
            userService.registerUser(user);
            return "redirect:/login";
        }
        catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "register";
        }

    }
    @PostMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

}
