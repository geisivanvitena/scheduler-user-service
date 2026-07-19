package com.geisivan.userservice.application.validator;


import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.exception.custom.ConflictException;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateEmailUpdate_shouldDoNothing_whenEmailIsNull() {

        User user = new User();
        user.setEmail("user@gmail.com");

        userValidator.validateEmailUpdate(user, null);

        verifyNoInteractions(userRepository);
    }

    @Test
    void validateEmailUpdate_shouldDoNothing_whenEmailIsTheSame() {

        User user = new User();
        user.setEmail("user@gmail.com");

        userValidator.validateEmailUpdate(user, "user@gmail.com");

        verifyNoInteractions(userRepository);
    }

    @Test
    void validateEmailUpdate_shouldValidateEmail_whenEmailIsDifferent() {

        User user = new User();
        user.setEmail("user@gmail.com");

        when(userRepository.existsByEmailIgnoreCase("new@gmail.com"))
                .thenReturn(false);

        userValidator.validateEmailUpdate(user, "new@gmail.com");

        verify(userRepository)
                .existsByEmailIgnoreCase("new@gmail.com");
    }

    @Test
    void validateEmailExists_shouldThrowException_whenEmailAlreadyExists() {

        String email = "existing@gmail.com";

        when(userRepository.existsByEmailIgnoreCase(email))
                .thenReturn(true);

        assertThrows(ConflictException.class,
                () -> userValidator.validateEmailExists(email));

        verify(userRepository).existsByEmailIgnoreCase(email);
    }

    @Test
    void validateEmailExists_shouldNotThrowException_whenEmailDoesNotExist() {

        String email = "available@gmail.com";

        when(userRepository.existsByEmailIgnoreCase(email))
                .thenReturn(false);

        assertDoesNotThrow(
                () -> userValidator.validateEmailExists(email));

        verify(userRepository).existsByEmailIgnoreCase(email);
    }
}
