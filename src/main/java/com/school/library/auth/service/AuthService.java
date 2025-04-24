package com.school.library.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.library.auth.config.JwtService;
import com.school.library.auth.dto.AuthResponseDTO;
import com.school.library.auth.dto.ChangePasswordDTO;
import com.school.library.auth.dto.LoginRequestDTO;
import com.school.library.auth.dto.RefreshTokenDTO;
import com.school.library.auth.exception.InvalidCredentialsException;
import com.school.library.auth.model.User;
import com.school.library.auth.repository.UserRepository;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthResponseDTO login(LoginRequestDTO req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        String refrehToken = jwtService.generateRefreshToken(user);
        return new AuthResponseDTO(token, refrehToken);
    }

    public RefreshTokenDTO refreshToken(RefreshTokenDTO req) {
        String token = req.getToken();

        if (!jwtService.isRefreshTokenValid(token)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String userId = jwtService.getUserIdFromRefresh(token);
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String newAccessToken = jwtService.generateToken(user);

        return RefreshTokenDTO.builder()
                .token(newAccessToken)
                .build();
    }

    public void changePassword(ChangePasswordDTO req) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }    
}
