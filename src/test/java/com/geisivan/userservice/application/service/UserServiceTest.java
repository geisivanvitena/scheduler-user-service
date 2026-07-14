package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.impl.UserServiceImpl;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String EMAIL = "teste@gmail.com";

    private User user;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(1L);
        user.setName("User test");
        user.setEmail(EMAIL);
        user.setStatus(UserStatus.ACTIVE);

        user.setRoles(Set.of());
        user.setAddresses(new ArrayList<>());
        user.setPhones(new ArrayList<>());
    }

    @Test
    void findAuthenticatedUser_shouldReturnUser_whenUserExists() {

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

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(userMapper.toDTO(user))
                .thenReturn(response);

        var result = userServiceImpl.findAuthenticatedUser();

        assertNotNull(result);
        assertEquals(EMAIL, result.email());

        verify(currentUserService).getAuthenticatedUser();
        verify(userMapper).toDTO(user);
    }

    @Test
    void updateAuthenticatedUser_shouldReturnUpdatedUser_whenUserExists() {

        UserUpdateRequestDTO dto =
                new UserUpdateRequestDTO(
                        "Updated User",
                        "updated@gmail.com",
                        null,
                        null,
                        null
                );

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "Updated User",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(userMapper.toDTO(user))
                .thenReturn(response);

        var result =
                userServiceImpl.updateAuthenticatedUser(dto);

        assertNotNull(result);
        assertEquals("Updated User", result.name());
        assertEquals("updated@gmail.com", result.email());

        verify(currentUserService).getAuthenticatedUser();
        verify(userMapper).update(dto, user);
        verify(userRepository).flush();
        verify(userMapper).toDTO(user);
    }

    @Test
    void updateAuthenticatedUser_shouldEncodePassword_whenPasswordIsProvided() {

        UserUpdateRequestDTO dto =
                new UserUpdateRequestDTO(
                        "Updated User",
                        "updated@gmail.com",
                        "123456",
                        null,
                        null
                );

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "Updated User",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded-password");

        when(userMapper.toDTO(user))
                .thenReturn(response);

        var result =
                userServiceImpl.updateAuthenticatedUser(dto);

        assertNotNull(result);

        verify(currentUserService).getAuthenticatedUser();
        verify(userMapper).update(dto, user);
        verify(passwordEncoder).encode("123456");
        verify(userRepository).flush();
        verify(userMapper).toDTO(user);

        assertEquals("encoded-password", user.getPassword());
    }

    @Test
    void updateAuthenticatedUser_shouldNotEncodePassword_whenPasswordIsNull() {

        UserUpdateRequestDTO dto =
                new UserUpdateRequestDTO(
                        "Updated User",
                        "updated@gmail.com",
                        null,
                        null,
                        null
                );

        UserResponseDTO response =
                new UserResponseDTO(
                       1L,
                        "Updated User",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(userMapper.toDTO(user))
                .thenReturn(response);

        var result =
                userServiceImpl.updateAuthenticatedUser(dto);

        assertNotNull(result);
        verify(currentUserService).getAuthenticatedUser();
        verify(userMapper).update(dto, user);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).flush();
        verify(userMapper).toDTO(user);
    }

    @Test
    void updateAuthenticatedUser_shouldNotEncodePassword_whenPasswordIsBlank() {

        UserUpdateRequestDTO dto =
                new UserUpdateRequestDTO(
                        "Updated User",
                        "updated@gmail.com",
                        "",
                        null,
                        null
                );

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "Updated User",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(userMapper.toDTO(user))
                .thenReturn(response);

        var result =
                userServiceImpl.updateAuthenticatedUser(dto);

        assertNotNull(result);

        verify(currentUserService).getAuthenticatedUser();
        verify(userMapper).update(dto, user);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).flush();
        verify(userMapper).toDTO(user);
    }

    @Test
    void deleteAuthenticatedUser_shouldDeleteUser_whenUserExists() {

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        userServiceImpl.deleteAuthenticatedUser();

        verify(currentUserService).getAuthenticatedUser();
        verify(userRepository).delete(user);
    }
}

