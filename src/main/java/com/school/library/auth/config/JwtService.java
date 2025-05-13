package com.school.library.auth.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.school.library.auth.model.User;
import com.school.library.member.model.Member;
import com.school.library.member.repository.MemberRepository;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;
    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;    

    @Autowired
    private MemberRepository memberRepository;

    public String generateToken(User user) {
        Member member = memberRepository.findByUser(user)
        .orElseThrow(() -> new RuntimeException("Member tidak ditemukan"));
    
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .claim("memberId", member.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey(secret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    
            if (claims.getExpiration().before(new Date())) {
                return false;
            }    
            return true;
        } catch (JwtException e) {
            log.error("Token validation failed: " + e.getMessage());            
            return false;           
        }
    }

    public String getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    private Key getSignKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getSignKey(refreshSecret), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey(refreshSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    
            if (claims.getExpiration().before(new Date())) {
                return false;
            }    
            return true;
        } catch (JwtException e) {
            log.error("Token validation failed: " + e.getMessage());            
            return false;           
        }
    }
    
    public String getUserIdFromRefresh(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey(refreshSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
