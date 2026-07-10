package com.example.lostfoundMS.controllers;

import com.example.lostfoundMS.entities.dto.ForgotPasswordRequest;
import com.example.lostfoundMS.entities.dto.RegisterRequest;
import com.example.lostfoundMS.entities.dto.ResetPasswordRequest;
import com.example.lostfoundMS.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private AuthService  authService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            authService.register(request);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("error", e.getMessage());
            return "register";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Check your email (including spam/junk) to verify your account before logging in."
        );
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, RedirectAttributes redirectAttributes) {
        try {
            authService.verifyEmail(token);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Email verified! You can now log in."
            );
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/login";
    }

    @GetMapping("/resend-verification")
    public String showResendForm(){
        return "resend-verification";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            authService.resendVerificationEmail(email);
            redirectAttributes.addFlashAttribute("successMessage", "Verification email sent. Check your inbox and spam folder.");        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @Valid @ModelAttribute ForgotPasswordRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "forgot-password";
        }
        authService.forgotPassword(request.getEmail());
        redirectAttributes.addFlashAttribute(
                "successMessage", "If that email is registered, a reset link has been sent."
        );
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        model.addAttribute("resetPasswordRequest", request);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @Valid @ModelAttribute ResetPasswordRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "reset-password";
        }
        try {
            authService.resetPassword(request);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("error", e.getMessage());
            return "reset-password";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Password updated. You can now log in.");
        return "redirect:/login";
    }
}
