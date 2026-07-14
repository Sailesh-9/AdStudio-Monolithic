package com.cts.adstudio.iam.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.adstudio.iam.dto.request.LoginRequest;
import com.cts.adstudio.iam.dto.request.RegisterRequest;
import com.cts.adstudio.iam.dto.response.LoginResponse;
import com.cts.adstudio.iam.dto.response.UserResponse;
import com.cts.adstudio.iam.entity.User;
import com.cts.adstudio.iam.enums.UserStatus;
import com.cts.adstudio.iam.exception.DuplicateResourceException;
import com.cts.adstudio.iam.repository.UserRepository;
import com.cts.adstudio.iam.security.JwtService;
import com.cts.adstudio.iam.service.AuditLogService;
import com.cts.adstudio.iam.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("A user already exists with email: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
//                .accountId(request.getUserId()) // this should be generated automatically
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
                .build();


        User savedTemp = userRepository.save(user);  // INSERT -> userId assigned automatically by MySQL
        savedTemp.setAccountId(savedTemp.getUserId());   // account_id = user_id
        User saved =  userRepository.save(savedTemp);

        log.info("Registered user id={} email={} role={}", saved.getUserId(), saved.getEmail(), saved.getRole());

        auditLogService.record(saved.getUserId(), "REGISTER_USER", "User");

        return toUserResponse(saved);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Throws an AuthenticationException (handled globally) on bad credentials,
        // inactive (disabled) or suspended (locked) accounts.
        

        // this will automatically call the line 23 of com.cts.adstudio.iam.security.CustomUserDetailsService
        /* 
        You only need to plug in:
            UserDetailsService
            PasswordEncoder
        remaining actions including password matching to DB also done by spring security by 
        by the below line "authenticationManager.authenticate("
        */
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + request.getEmail()));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());

        auditLogService.record(user.getUserId(), "LOGIN", "User");
        log.info("User logged in id={} email={}", user.getUserId(), user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .expiresInMs(jwtService.getExpirationMs())
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .accountId(user.getAccountId())
                .status(user.getStatus())
                .build();
    }
}
