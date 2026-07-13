package com.geisivan.userservice.application.service;

import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.util.AuthenticatedUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(1L);
        user.setName("User test");
        user.setEmail("teste@gmail.com");
        user.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void getAuthenticatedUser_shouldReturnUser_whenUserExists() {

        Long id = 1L;

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId)
                    .thenReturn(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));

            var result = currentUserService.getAuthenticatedUser();

            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals("teste@gmail.com", result.getEmail());

            verify(userRepository).findById(id);
        }
    }

    @Test
    void getAuthenticatedUser_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {

        Long id = 1L;

        try (MockedStatic<AuthenticatedUserUtil> mocked =
                     Mockito.mockStatic(AuthenticatedUserUtil.class)) {

            mocked.when(AuthenticatedUserUtil::getId)
                    .thenReturn(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> currentUserService.getAuthenticatedUser());

            verify(userRepository).findById(id);
        }
    }
}
