package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.UserService;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import com.geisivan.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserService userService;

    private static final String BASE_URL = "/api/v1/users/me";
    private static final String EMAIL = "teste@gmail.com";

    private static final String VALID_UPDATE_REQUEST = """
        {
           "name":"Updated User",
           "email":"admin@domain.com"
        }
        """;

    @Test
    void findAuthenticatedUser_shouldReturn200_whenUserExists()
            throws Exception {

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "User test",
                        EMAIL,
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        null
                );

        when(userService.findAuthenticatedUser())
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(1))

                .andExpect(jsonPath("$.name")
                        .value("User test"))

                .andExpect(jsonPath("$.email")
                        .value("teste@gmail.com"))

                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        verify(userService).findAuthenticatedUser();
    }

    @Test
    void findAuthenticatedUser_shouldReturn404_whenUserDoesNotExist()
            throws Exception {

        when(userService.findAuthenticatedUser())
                .thenThrow(new ResourceNotFoundException(
                        "User not found"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isNotFound());

        verify(userService).findAuthenticatedUser();
    }

    @Test
    void updateAuthenticatedUser_shouldReturn200_whenRequestIsValid()
            throws Exception {

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "Updated User",
                        EMAIL,
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        when(userService.updateAuthenticatedUser(
                any(UserUpdateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_UPDATE_REQUEST))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(1))

                .andExpect(jsonPath("$.name")
                        .value("Updated User"))

                .andExpect(jsonPath("$.email")
                        .value(EMAIL))

                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        verify(userService)
                .updateAuthenticatedUser(any(UserUpdateRequestDTO.class));
    }

    @Test
    void updateAuthenticatedUser_shouldReturn404_whenUserDoesNotExist()
            throws Exception {

        when(userService.updateAuthenticatedUser(
                any(UserUpdateRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "User not found"));

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_UPDATE_REQUEST))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("User not found"));

        verify(userService)
                .updateAuthenticatedUser(
                        any(UserUpdateRequestDTO.class));
    }
}
