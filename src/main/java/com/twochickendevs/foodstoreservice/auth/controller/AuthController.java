package com.twochickendevs.foodstoreservice.auth.controller;

import com.twochickendevs.foodstoreservice.auth.dto.CreateUserRequest;
import com.twochickendevs.foodstoreservice.auth.dto.LoginRequest;
import com.twochickendevs.foodstoreservice.auth.dto.RefreshTokenRequest;
import com.twochickendevs.foodstoreservice.auth.dto.TokenResponse;
import com.twochickendevs.foodstoreservice.auth.dto.UserResponse;
import com.twochickendevs.foodstoreservice.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
