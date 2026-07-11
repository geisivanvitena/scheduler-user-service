package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.impl.UserServiceImpl;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.util.AuthenticatedUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;

    private static final String EMAIL = "teste@gmail.com";

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

        Long id = 1L;

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

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId)
                    .thenReturn(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));

            when(userMapper.toDTO(user))
                    .thenReturn(response);

            var result = userServiceImpl.findAuthenticatedUser();

            assertNotNull(result);
            assertEquals(EMAIL, result.email());

            verify(userRepository).findById(id);
            verify(userMapper).toDTO(user);
        }
    }

    @Test
    void findAuthenticatedUser_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {

        Long id = 1L;

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId)
                    .thenReturn(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userServiceImpl.findAuthenticatedUser());

            verify(userRepository).findById(id);
            verifyNoInteractions(userMapper);
        }
    }

    @Test
    void updateAuthenticatedUser_shouldReturnUpdatedUser_whenUserExists() {

        Long id = 1L;

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
                        id,
                        "Updated User",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId)
                    .thenReturn(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));

            when(userMapper.toDTO(user))
                    .thenReturn(response);

            var result =
                    userServiceImpl.updateAuthenticatedUser(dto);

            assertNotNull(result);
            assertEquals("Updated User", result.name());
            assertEquals("updated@gmail.com", result.email());

            verify(userRepository).findById(id);
            verify(userMapper).update(dto, user);
            verify(userRepository).flush();
            verify(userMapper).toDTO(user);
        }
    }

    @Test
    void updateAuthenticatedUser_shouldEncodePassword_whenPasswordIsProvided() {

        Long id = 1L;

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
                        id,
                        "Updated User",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId)
                    .thenReturn(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode("123456"))
                    .thenReturn("encoded-password");

            when(userMapper.toDTO(user))
                    .thenReturn(response);

            var result = userServiceImpl.updateAuthenticatedUser(dto);

            assertNotNull(result);

            verify(userRepository).findById(id);
            verify(userMapper).update(dto, user);
            verify(passwordEncoder).encode("123456");
            verify(userRepository).flush();
            verify(userMapper).toDTO(user);

            assertEquals( "encoded-password", user.getPassword());
        }
    }

    @Test
    void updateAuthenticatedUser_shouldNotEncodePassword_whenPasswordIsNull() {

        Long id = 1L;

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
                        id,
                        "Updated User",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId)
                    .thenReturn(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));

            when(userMapper.toDTO(user))
                    .thenReturn(response);

            var result =
                    userServiceImpl.updateAuthenticatedUser(dto);

            assertNotNull(result);

            verify(userRepository).findById(id);
            verify(userMapper).update(dto, user);
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository).flush();
            verify(userMapper).toDTO(user);
        }
    }

    @Test
    void updateAuthenticatedUser_shouldNotEncodePassword_whenPasswordIsBlank() {

        Long id = 1L;

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
                        id,
                        "Updated User",
                        "updated@gmail.com",
                        UserStatus.ACTIVE,
                        Set.of(),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        Instant.now()
                );

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId)
                    .thenReturn(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));

            when(userMapper.toDTO(user))
                    .thenReturn(response);

            var result =
                    userServiceImpl.updateAuthenticatedUser(dto);

            assertNotNull(result);

            verify(userRepository).findById(id);
            verify(userMapper).update(dto, user);
            verify(passwordEncoder, never())
                    .encode(anyString());
            verify(userRepository).flush();
            verify(userMapper).toDTO(user);
        }
    }

    @Test
    void updateAuthenticatedUser_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {

        Long id = 1L;

        UserUpdateRequestDTO dto =
                new UserUpdateRequestDTO(
                        "Updated User",
                        "updated@gmail.com",
                        null,
                        null,
                        null
                );

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId).thenReturn(id);

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userServiceImpl.updateAuthenticatedUser(dto));

            verify(userRepository).findById(id);
            verifyNoInteractions(userMapper);
        }
    }
}

