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
import com.school.library.auth.dto.RegisterRequestDTO;
import com.school.library.auth.exception.InvalidCredentialsException;
import com.school.library.auth.model.Role;
import com.school.library.auth.model.User;
import com.school.library.auth.repository.UserRepository;
import com.school.library.member.model.Member;
import com.school.library.member.repository.MemberRepository;

import jakarta.transaction.Transactional;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MemberRepository memberRepository;

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

    @Transactional
    public void register(RegisterRequestDTO req){
        userRepository.findByEmail(req.getEmail())
        .ifPresent(user -> {
            throw new RuntimeException("Email already exists");
        });

        User newUser = User.builder()
        .name(req.getName())
        .email(req.getEmail())
        .password(passwordEncoder.encode(req.getPassword()))
        .role(Role.MEMBER)
        .build();

        userRepository.save(newUser);
        
        Member newMember = Member.builder()
        .address(req.getAddress())
        .phone(req.getPhone())
        .user(newUser)
        .build();

        memberRepository.save(newMember);
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
