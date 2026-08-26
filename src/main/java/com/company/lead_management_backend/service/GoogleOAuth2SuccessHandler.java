package com.company.lead_management_backend.service;

import com.company.lead_management_backend.dto.AuthResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    public GoogleOAuth2SuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();

        String name = googleUser.getAttribute("name");
        String email = googleUser.getAttribute("email");

        AuthResponse authResponse = authService.googleLogin(name, email);

        response.setContentType("application/json");
        response.getWriter().write(
                "{\"token\":\"" + authResponse.getToken() +
                "\",\"email\":\"" + authResponse.getEmail() + "\"}"
        );
    }
}