package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.AdminUserService;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ConflictException;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import com.geisivan.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private AdminUserService adminUserService;

    private static final String BASE_URL = "/api/v1/admin/users";
    private static final String EMAIL = "admin.test@email.com";

    private static final String VALID_REQUEST = """
            {
                "name": "Admin Test",
                "email": "admin.test@email.com",
                "password": "123456",
                "roles": [
                    "ROLE_ADMIN"
                ],
                "status": "ACTIVE",
                "addresses": [],
                "phones": []
            }
            """;

    @Test
    void createUser_shouldReturn201_whenRequestIsValid()
            throws Exception {

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "Admin Test",
                        EMAIL,
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        null
                );

        when(adminUserService.createUser(any(AdminUserRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.name").value("Admin Test"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(adminUserService)
                .createUser(any(AdminUserRequestDTO.class));
    }

    @Test
    void createUser_shouldReturn409_whenEmailAlreadyExists()
            throws Exception {

        when(adminUserService.createUser(any(AdminUserRequestDTO.class)))
                .thenThrow(new ConflictException(
                        "Email already exists"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))

                .andExpect(status().isConflict())

                .andExpect(jsonPath("$.message")
                        .value("Email already exists"));

        verify(adminUserService)
                .createUser(any(AdminUserRequestDTO.class));
    }

    @Test
    void createUser_shouldReturn404_whenRoleDoesNotExist()
            throws Exception {

        when(adminUserService.createUser(any(AdminUserRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Role ROLE_ADMIN not found"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("Role ROLE_ADMIN not found"));

        verify(adminUserService)
                .createUser(any(AdminUserRequestDTO.class));
    }

    @Test
    void findAllUsers_shouldReturn200_whenUsersExist()
            throws Exception {

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "Admin Test",
                        EMAIL,
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        null
                );

        Page<UserResponseDTO> page =
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 10),
                        1
                );

        when(adminUserService.findAllUsers(any()))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content[0].email")
                        .value(EMAIL))

                .andExpect(jsonPath("$.content[0].name")
                        .value("Admin Test"))

                .andExpect(jsonPath("$.totalElements")
                        .value(1));

        verify(adminUserService).findAllUsers(any());
    }

    @Test
    void findAllUsers_shouldReturnEmptyPage_whenUsersDoNotExist()
            throws Exception {

        Page<UserResponseDTO> page =
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                );

        when(adminUserService.findAllUsers(any()))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements")
                        .value(0));

        verify(adminUserService).findAllUsers(any());
    }
}
