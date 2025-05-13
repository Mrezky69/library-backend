package com.school.library.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.library.auth.config.JwtService;
import com.school.library.auth.controller.AuthController;
import com.school.library.auth.dto.*;
import com.school.library.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin() throws Exception {
        AuthResponseDTO response = new AuthResponseDTO("token", "refresh");
        Mockito.when(authService.login(any())).thenReturn(response);

        LoginRequestDTO request = new LoginRequestDTO("email@test.com", "123");

        mockMvc.perform(post("/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    void testRegister() throws Exception {
        RegisterRequestDTO req = RegisterRequestDTO.builder()
                .name("John").email("john@mail.com").password("123")
                .phone("123").address("address").build();

        mockMvc.perform(post("/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testRefreshToken() throws Exception {
        RefreshTokenDTO input = new RefreshTokenDTO("refreshOld");
        RefreshTokenDTO output = new RefreshTokenDTO("refreshNew");

        Mockito.when(authService.refreshToken(any())).thenReturn(output);

        mockMvc.perform(post("/v1/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("refreshNew"));
    }

    @Test
    @WithMockUser(roles = {"MEMBER"})
    void testChangePassword() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO("old", "new");

        mockMvc.perform(post("/v1/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
