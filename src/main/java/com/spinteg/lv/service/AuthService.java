package com.spinteg.lv.service;

import com.spinteg.lv.dto.*;
import com.spinteg.lv.entity.User;
import com.spinteg.lv.entity.enums.Role;
import com.spinteg.lv.repository.UserRepository;
import com.spinteg.lv.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===========================
    // LOGIN
    // ===========================
    public JwtResponse authenticateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return new JwtResponse(
                jwt,
                "Bearer",
                user.getFullName(),
                user.getEmail(),
                roles
        );
    }

    // ===========================
    // SIGNUP
    // ===========================
    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {

        // Email Unique Check
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email already in use"));
        }

        // Create User
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setCompanyName(signUpRequest.getCompanyName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Roles
        Set<Role> roles = new HashSet<>();
        if (signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
            roles.add(Role.ROLE_DEPLOYER);
        } else {
            signUpRequest.getRoles().forEach(r -> roles.add(Role.valueOf(r)));
        }
        user.setRoles(roles);

        // Save
        try {
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error during registration: " + e.getMessage()));
        }
    }
}
