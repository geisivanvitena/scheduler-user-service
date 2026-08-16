package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PageResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.AdminUserService;
import com.geisivan.userservice.controller.AdminUserController;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.geisivan.userservice.application.dto.request.UserStatusUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRoleUpdateRequestDTO;

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

        PageResponseDTO<UserResponseDTO> pageResponse =
                new PageResponseDTO<>(
                        List.of(response),
                        0,
                        10,
                        1,
                        1
                );

        when(adminUserService.findAllUsers(any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content[0].email")
                        .value(EMAIL))

                .andExpect(jsonPath("$.content[0].name")
                        .value("Admin Test"))

                .andExpect(jsonPath("$.totalElements")
                        .value(1))

                .andExpect(jsonPath("$.totalPages")
                        .value(1)
        );




        verify(adminUserService).findAllUsers(any(), any(), any());
    }

    @Test
    void findAllUsers_shouldReturnEmptyPage_whenUsersDoNotExist()
            throws Exception {

       PageResponseDTO<UserResponseDTO> page =
                new PageResponseDTO<>(
                        List.of(),
                        0,
                        10,
                        0,
                        0
                );

        when(adminUserService.findAllUsers(any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements")
                        .value(0));

        verify(adminUserService).findAllUsers(any(), any(), any());
    }

    @Test
    void findUserById_shouldReturn200_whenUserExists()
            throws Exception {

        Long userId = 1L;

        UserResponseDTO response =
                new UserResponseDTO(
                        userId,
                        "Admin Test",
                        EMAIL,
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        null
                );

        when(adminUserService.findUserById(userId))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + userId))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(userId))

                .andExpect(jsonPath("$.email")
                        .value(EMAIL))

                .andExpect(jsonPath("$.name")
                        .value("Admin Test"))

                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        verify(adminUserService).findUserById(userId);
    }

    @Test
    void findUserById_shouldReturn404_whenUserDoesNotExist()
            throws Exception {

        Long userId = 99L;

        when(adminUserService.findUserById(userId))
                .thenThrow(new ResourceNotFoundException(
                        "User with id " + userId + " not found"));

        mockMvc.perform(get(BASE_URL + "/" + userId))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("User with id 99 not found"));

        verify(adminUserService)
                .findUserById(userId);
    }

    @Test
    void findUserByEmail_shouldReturn200_whenUserExists()
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

        when(adminUserService.findUserByEmail(EMAIL))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/email")
                        .param("email", EMAIL))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.email")
                        .value(EMAIL))

                .andExpect(jsonPath("$.name")
                        .value("Admin Test"));

        verify(adminUserService).findUserByEmail(EMAIL);
    }

    @Test
    void findUserByEmail_shouldReturn404_whenUserDoesNotExist()
            throws Exception {

        when(adminUserService.findUserByEmail(EMAIL))
                .thenThrow(new ResourceNotFoundException(
                        "User with email " + EMAIL + " not found"));

        mockMvc.perform(get(BASE_URL + "/email")
                        .param("email", EMAIL))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("User with email " + EMAIL + " not found"));

        verify(adminUserService)
                .findUserByEmail(EMAIL);
    }

    @Test
    void updateUser_shouldReturn200_whenRequestIsValid()
            throws Exception {

        Long userId = 1L;

        String updateRequest = """
            {
                "name": "Admin Updated",
                "email": "updated@gmail.com",
                "password": "123456"
            }
            """;

        UserResponseDTO response =
                new UserResponseDTO(
                        userId,
                        "Admin Updated",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        null
                );

        when(adminUserService.updateUser(
                eq(userId),
                any(UserUpdateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(userId))

                .andExpect(jsonPath("$.name")
                        .value("Admin Updated"))

                .andExpect(jsonPath("$.email")
                        .value("updated@gmail.com"));

        verify(adminUserService).updateUser(
                eq(userId), any(UserUpdateRequestDTO.class));
    }

    @Test
    void updateUser_shouldReturn404_whenUserDoesNotExist()
            throws Exception {

        Long userId = 99L;

        String updateRequest = """
            {
                "name": "Admin Updated",
                "email": "updated@gmail.com",
                "password": "123456"
            }
            """;

        when(adminUserService.updateUser(
                eq(userId),
                any(UserUpdateRequestDTO.class)))

                .thenThrow(new ResourceNotFoundException(
                        "User with id " + userId + " not found"
                ));

        mockMvc.perform(put(BASE_URL + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("User with id 99 not found"));

        verify(adminUserService).updateUser(
                eq(userId), any(UserUpdateRequestDTO.class));
    }

    @Test
    void updateUser_shouldReturn409_whenEmailAlreadyExists()
            throws Exception {

        Long userId = 1L;

        String updateRequest = """
            {
                "name": "Admin Updated",
                "email": "existing@gmail.com",
                "password": "123456"
            }
            """;

        when(adminUserService.updateUser(
                eq(userId),
                any(UserUpdateRequestDTO.class)))

                .thenThrow(new ConflictException(
                        "Email already exists"
                ));

        mockMvc.perform(put(BASE_URL + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))

                .andExpect(status().isConflict())

                .andExpect(jsonPath("$.message")
                        .value("Email already exists"));

        verify(adminUserService).updateUser(
                eq(userId), any(UserUpdateRequestDTO.class));
    }

    @Test
    void updateUserStatus_shouldReturn200_whenStatusUpdated()
            throws Exception {

        when(adminUserService.updateUserStatus(
                eq(1L),
                any(UserStatusUpdateRequestDTO.class)))

                .thenReturn(createResponse());

        mockMvc.perform(patch(BASE_URL + "/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                       "status":"PENDING"
                    }
                    """))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("PENDING"));

        verify(adminUserService).updateUserStatus(
                eq(1L), any(UserStatusUpdateRequestDTO.class));
    }

    @Test
    void updateUserStatus_shouldReturn404_whenUserNotFound()
            throws Exception {

        when(adminUserService.updateUserStatus(
                eq(99L),
                any(UserStatusUpdateRequestDTO.class)))

                .thenThrow(new ResourceNotFoundException(
                        "User with ID 99 not found"));

        mockMvc.perform(patch(BASE_URL + "/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "status":"ACTIVE"
                    }
                    """))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("User with ID 99 not found"));

        verify(adminUserService).updateUserStatus(
                eq(99L), any(UserStatusUpdateRequestDTO.class));
    }

    private UserResponseDTO createResponse() {

        return new UserResponseDTO(
                1L,
                "Admin Test",
                EMAIL,
                UserStatus.PENDING,
                Set.of(),
                List.of(),
                List.of(),
                Instant.now(),
                null
        );
    }

    @Test
    void updateUserRole_shouldReturn200_whenRolesUpdated()
            throws Exception {

        when(adminUserService.updateUserRole(
                eq(1L), any(UserRoleUpdateRequestDTO.class)))
                .thenReturn(createAdminResponse());

        mockMvc.perform(patch(BASE_URL + "/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "roles": ["ROLE_ADMIN"]
                    }
                    """))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(EMAIL));

        verify(adminUserService).updateUserRole(eq(1L),
                any(UserRoleUpdateRequestDTO.class));
    }

    @Test
    void updateUserRole_shouldReturn404_whenUserNotFound()
            throws Exception {

        when(adminUserService.updateUserRole(
                eq(99L), any(UserRoleUpdateRequestDTO.class))).thenThrow(
                        new ResourceNotFoundException("User with ID 99 not found"));

        mockMvc.perform(patch(BASE_URL + "/99/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "roles": ["ROLE_ADMIN"]
                    }
                    """))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("User with ID 99 not found"));

        verify(adminUserService).updateUserRole(eq(99L),
                any(UserRoleUpdateRequestDTO.class));
    }

    @Test
    void updateUserRole_shouldReturn404_whenRoleNotFound()
            throws Exception {

        when(adminUserService.updateUserRole(
                eq(1L), any(UserRoleUpdateRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Role ROLE_ADMIN not found"));

        mockMvc.perform(patch(BASE_URL + "/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "roles": ["ROLE_ADMIN"]
                    }
                    """))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Role ROLE_ADMIN not found"));

        verify(adminUserService).updateUserRole(eq(1L),
                any(UserRoleUpdateRequestDTO.class));
    }

    @Test
    void deleteUser_shouldReturn204_whenUserExists()
            throws Exception {

        Long userId = 1L;

        doNothing()
                .when(adminUserService)
                .deleteUser(userId);

        mockMvc.perform(delete(BASE_URL + "/" + userId))

                .andExpect(status().isNoContent());

        verify(adminUserService).deleteUser(userId);
    }

    @Test
    void deleteUser_shouldReturn404_whenUserDoesNotExist()
            throws Exception {

        Long userId = 99L;

        doThrow(new ResourceNotFoundException(
                "User with ID 99 not found"))
                .when(adminUserService)
                .deleteUser(userId);

        mockMvc.perform(delete(BASE_URL + "/" + userId))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("User with ID 99 not found"));

        verify(adminUserService).deleteUser(userId);
    }

    private UserResponseDTO createAdminResponse() {

        return new UserResponseDTO(
                1L,
                "Admin Test",
                EMAIL,
                UserStatus.ACTIVE,
                Set.of(),
                List.of(),
                List.of(),
                Instant.now(),
                Instant.now()
        );
    }
}
