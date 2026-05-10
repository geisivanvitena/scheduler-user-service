package com.geisivan.userservice;

import com.geisivan.userservice.application.controller.UserController;
import com.geisivan.userservice.application.dto.request.LoginRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.LoginResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.UserService;
import com.geisivan.userservice.infrastructure.exception.ConflictException;
import com.geisivan.userservice.infrastructure.exception.UnauthorizedException;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import com.geisivan.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private static final String BASE_URL = "/users";
    private static final String EMAIL = "admin@domain.com";

    @Test
    void create_shouldReturn201() throws Exception {

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "User test",
                        EMAIL,
                        List.of(),
                        List.of()
                );

        when(service.create(any(UserRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                           "name": "User test",
                           "email": "admin@domain.com",
                           "password": "12345",
                           "address": [],
                           "phones": []
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(EMAIL));

        verify(service, times(1))
                .create(any(UserRequestDTO.class));
    }

    @Test
    void create_shouldReturn409_whenConflict() throws Exception {

        when(service.create(any(UserRequestDTO.class)))
                .thenThrow(new ConflictException(
                        "Email already exists"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                           "name": "User test",
                           "email": "admin@domain.com",
                           "password": "12345",
                           "address": [],
                           "phones": []
                        }
                        """))
                .andExpect(status().isConflict());

        verify(service, times(1))
                .create(any(UserRequestDTO.class));
    }

    @Test
    void login_shouldReturn200_whenValidCredentials()
            throws Exception {

        LoginResponseDTO response =
                new LoginResponseDTO(
                        "Bearer token",
                        1L,
                        EMAIL
                );

        when(service.authenticate(any(LoginRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                           "email":"admin@domain.com",
                           "password":"12345"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("Bearer token"))
                .andExpect(jsonPath("$.userId")
                        .value(1))
                .andExpect(jsonPath("$.email")
                        .value(EMAIL));

        verify(service).authenticate(any(LoginRequestDTO.class));
    }

    @Test
    void login_shouldReturn401_whenInvalidCredentials()
            throws Exception {

        when(service.authenticate(any(LoginRequestDTO.class )))
                .thenThrow(new UnauthorizedException(
                        "Invalid credentials"));

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                           "email":"admin@domain.com",
                           "password":"wrong"
                        }
                        """))
                .andExpect(status().isUnauthorized());

        verify(service).authenticate(any(LoginRequestDTO.class));
    }
}
