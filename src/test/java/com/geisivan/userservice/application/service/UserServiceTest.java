package com.geisivan.userservice.application.service;

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
}

