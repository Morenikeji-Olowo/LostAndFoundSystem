package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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

        try {
            User user = userService.loginUser(email, password);
            session.setAttribute("loggedInUser", user);
            return "redirect:/";
        } catch (RuntimeException e) {
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
            if(!StringUtils.hasText(user.getEmail()) || !StringUtils.hasText(user.getPassword())
                    || !StringUtils.hasText(user.getConfirmPassword()) || !StringUtils.hasText(user.getUniversityId())){
                model.addAttribute("error", "All fields are required");
                return "register";
            }
            if(!user.getPassword().equals(user.getConfirmPassword())){
                model.addAttribute("error", "Passwords do not match");
                return "register";
            }
            if(userService.getUser(user.getEmail()) != null){
                model.addAttribute("error","User already exists");
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
