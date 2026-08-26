package com.company.lead_management_backend.controller;

import com.company.lead_management_backend.dto.AuthResponse;
import com.company.lead_management_backend.dto.LoginRequest;
import com.company.lead_management_backend.dto.RegisterRequest;
import com.company.lead_management_backend.model.User;
import com.company.lead_management_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}