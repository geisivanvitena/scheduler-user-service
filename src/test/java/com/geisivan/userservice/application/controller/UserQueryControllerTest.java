package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.UserService;
import com.geisivan.userservice.controller.UserQueryController;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import com.geisivan.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserQueryControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserService userService;

    private static final String BASE_URL = "/api/v1/internal/users";

    private static final String EMAIL = "teste@gmail.com";

    @Test
    void findUserById_shouldReturn200_whenUserExists()
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

        when(userService.findUserById(1L))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/1"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(1))

                .andExpect(jsonPath("$.name")
                        .value("User test"))

                .andExpect(jsonPath("$.email")
                        .value(EMAIL))

                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        verify(userService).findUserById(1L);
    }

    @Test
    void findUserById_shouldReturn404_whenUserDoesNotExist()
            throws Exception {

        when(userService.findUserById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "User not found"));

        mockMvc.perform(get(BASE_URL + "/999"))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("User not found"));

        verify(userService).findUserById(999L);
    }
}
