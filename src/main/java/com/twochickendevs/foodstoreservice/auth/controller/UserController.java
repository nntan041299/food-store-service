package com.twochickendevs.foodstoreservice.auth.controller;

import com.twochickendevs.foodstoreservice.auth.dto.UpdateUserRequest;
import com.twochickendevs.foodstoreservice.auth.dto.UserResponse;
import com.twochickendevs.foodstoreservice.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/current-user")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PutMapping("/current-user")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(authService.updateCurrentUser(request));
    }
}
