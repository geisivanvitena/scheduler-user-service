package com.geisivan.userservice.application.validator;

import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.exception.custom.ConflictException;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateEmailUpdate(User user, String email) {

        if (email != null
                && !email.equalsIgnoreCase(user.getEmail())) {

            validateEmailExists(email);
        }
    }

    public void validateEmailExists(String email) {

        if (userRepository.existsByEmailIgnoreCase(email)) {

            throw new ConflictException(
                    "Email " + email + " already exists");
        }
    }
}
