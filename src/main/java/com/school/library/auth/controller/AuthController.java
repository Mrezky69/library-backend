package com.school.library.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.library.auth.dto.AuthResponseDTO;
import com.school.library.auth.dto.ChangePasswordDTO;
import com.school.library.auth.dto.LoginRequestDTO;
import com.school.library.auth.dto.RefreshTokenDTO;
import com.school.library.auth.service.AuthService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/v1")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenDTO> refresh(@Valid @RequestBody RefreshTokenDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDTO req) {
        authService.changePassword(req);
        return ResponseEntity.ok().build();
    }    
}
