package com.example.lostfoundMS.security;

import com.example.lostfoundMS.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        String redirectUrl = switch (user.getRole()){
            case ADMIN -> "/admin/dashboard";
            case SECURITY_OFFICE -> "/security-office/dashboard";
            case STUDENT -> "/items";
        };

        response.sendRedirect(redirectUrl);
    }
}
