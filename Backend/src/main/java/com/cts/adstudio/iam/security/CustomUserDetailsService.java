package com.cts.adstudio.iam.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cts.adstudio.iam.entity.User;
import com.cts.adstudio.iam.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Loads a user by email for Spring Security authentication.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override // the line  "authenticationManager.authenticate(" from com.cts.adstudio.iam.service.impl.AuthServiceImpl
    // will call this below function automatically by the spring security
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new CustomUserDetails(user);
    }
}
