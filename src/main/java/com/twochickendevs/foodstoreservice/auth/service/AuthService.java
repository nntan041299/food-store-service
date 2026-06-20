package com.twochickendevs.foodstoreservice.auth.service;

import com.twochickendevs.foodstoreservice.auth.dto.CreateUserRequest;
import com.twochickendevs.foodstoreservice.auth.dto.LoginRequest;
import com.twochickendevs.foodstoreservice.auth.dto.RefreshTokenRequest;
import com.twochickendevs.foodstoreservice.auth.dto.TokenResponse;
import com.twochickendevs.foodstoreservice.auth.dto.UserResponse;
import com.twochickendevs.foodstoreservice.auth.entity.Role;
import com.twochickendevs.foodstoreservice.security.JwtUtil;
import com.twochickendevs.foodstoreservice.auth.entity.User;
import com.twochickendevs.foodstoreservice.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public UserResponse register(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(UserResponse::from)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public TokenResponse login(LoginRequest request) {
        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());

        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return TokenResponse.of(
                jwtUtil.generateAccessToken(user),
                jwtUtil.generateRefreshToken(user)
        );
    }

    public TokenResponse refresh(RefreshTokenRequest request) {
        String username = jwtUtil.extractUsername(request.getRefreshToken());
        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.isTokenValid(request.getRefreshToken(), user) || jwtUtil.isAccessToken(request.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        return TokenResponse.of(
                jwtUtil.generateAccessToken(user),
                jwtUtil.generateRefreshToken(user)
        );
    }
}
