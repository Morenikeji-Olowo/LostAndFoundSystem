package com.example.lostfoundMS.utils;

import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class AuthUtils {
    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser(HttpSession session){
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            throw new RuntimeException("No user loggged in");
        }
        return user;
    }

    public boolean isLoggedIn(HttpSession session){
       return session.getAttribute("loggedInUser") != null;
    }

    // add this to AuthUtils
    public void addAuthAttributes(HttpSession session, Model model) {
        if (isLoggedIn(session)) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("fullName",
                    getCurrentUser(session).getFullName());
            model.addAttribute("role",
                    getCurrentUser(session).getRole());
            // add role so navbar can display it
        } else {
            model.addAttribute("isLoggedIn", false);
        }
    }
}
