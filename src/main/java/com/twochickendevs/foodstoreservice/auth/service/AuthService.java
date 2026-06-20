package com.twochickendevs.foodstoreservice.auth.service;

import com.twochickendevs.foodstoreservice.auth.dto.CreateUserRequest;
import com.twochickendevs.foodstoreservice.auth.dto.LoginRequest;
import com.twochickendevs.foodstoreservice.auth.dto.RefreshTokenRequest;
import com.twochickendevs.foodstoreservice.auth.dto.TokenResponse;
import com.twochickendevs.foodstoreservice.auth.dto.UpdateUserRequest;
import com.twochickendevs.foodstoreservice.auth.dto.UserResponse;
import com.twochickendevs.foodstoreservice.auth.entity.Role;
import com.twochickendevs.foodstoreservice.auth.mapper.UserMapper;
import com.twochickendevs.foodstoreservice.security.JwtUtil;
import com.twochickendevs.foodstoreservice.auth.entity.User;
import com.twochickendevs.foodstoreservice.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
    private final UserMapper userMapper;

    public UserResponse register(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        if (request.getRole() != Role.CUSTOMER && request.getRole() != Role.SHOP_OWNER) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public UserResponse updateCurrentUser(UpdateUserRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName());
        }

        if (StringUtils.hasText(request.getNewPassword())) {
            if (!StringUtils.hasText(request.getCurrentPassword())) {
                throw new IllegalArgumentException("Current password is required to set a new password");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadCredentialsException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return userMapper.toResponse(userRepository.save(user));
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
