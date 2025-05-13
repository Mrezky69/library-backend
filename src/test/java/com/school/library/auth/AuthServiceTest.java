package com.school.library.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.school.library.auth.config.JwtService;
import com.school.library.auth.dto.*;
import com.school.library.auth.model.User;
import com.school.library.auth.repository.UserRepository;
import com.school.library.auth.service.AuthService;
import com.school.library.member.model.Member;
import com.school.library.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        LoginRequestDTO request = new LoginRequestDTO("test@mail.com", "password");
        User mockUser = User.builder().email("test@mail.com").password("encoded").build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtService.generateToken(mockUser)).thenReturn("access");
        when(jwtService.generateRefreshToken(mockUser)).thenReturn("refresh");

        AuthResponseDTO result = authService.login(request);

        assertEquals("access", result.getToken());
        assertEquals("refresh", result.getRefreshToken());
    }

    @Test
    void testLoginInvalidPassword() {
        User user = User.builder().email("test@mail.com").password("encoded").build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
            authService.login(new LoginRequestDTO("test@mail.com", "wrong"))
        );
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequestDTO req = RegisterRequestDTO.builder()
                .email("test@mail.com")
                .password("123")
                .name("Test")
                .address("Street 1")
                .phone("1234")
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("hashed");

        authService.register(req);

        verify(userRepository).save(any(User.class));
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void testRefreshTokenSuccess() {
        RefreshTokenDTO req = new RefreshTokenDTO("oldToken");
        User user = User.builder().id(1L).email("user@mail.com").build();

        when(jwtService.isRefreshTokenValid("oldToken")).thenReturn(true);
        when(jwtService.getUserIdFromRefresh("oldToken")).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("newAccess");

        RefreshTokenDTO result = authService.refreshToken(req);

        assertEquals("newAccess", result.getToken());
    }

    @Test
    void testChangePasswordSuccess() {
        User user = User.builder().email("user@mail.com").password("encoded").build();

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@mail.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

        ChangePasswordDTO dto = new ChangePasswordDTO("oldPass", "newPass");

        authService.changePassword(dto);

        assertEquals("newEncoded", user.getPassword());
        verify(userRepository).save(user);
    }
}
