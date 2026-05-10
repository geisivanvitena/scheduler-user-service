package com.geisivan.userservice;

import com.geisivan.userservice.application.converter.UserConverter;
import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.UserService;
import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.infrastructure.exception.ConflictException;
import com.geisivan.userservice.infrastructure.exception.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserConverter converter;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService service;

    private User user;
    private Role role;
    private static final String EMAIL = "teste@gmail.com";

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName(RoleName.ROLE_USER);

        user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setPassword("12345");

        user.setRoles(new HashSet<>());
        user.setAddress(new ArrayList<>());
        user.setPhones(new ArrayList<>());
    }

    @Test
    void create_shouldReturnUser() {

        UserRequestDTO dto =
                new UserRequestDTO(
                        "User test",
                        EMAIL,
                        "12345",
                        List.of(),
                        List.of()
                );

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "User test",
                        EMAIL,
                        List.of(),
                        List.of()
                );

        when(repository.existsByEmail(EMAIL)).thenReturn(false);
        when(encoder.encode("12345")).thenReturn("encoded");
        when(converter.toEntity(dto)).thenReturn(user);
        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.of(role));
        when(repository.save(user)).thenReturn(user);
        when(converter.toResponse(user)).thenReturn(response);

        var result = service.create(dto);

        assertNotNull(result);
        assertEquals(EMAIL, result.email());

        verify(repository).existsByEmail(EMAIL);
        verify(encoder).encode("12345");
        verify(roleRepository).findByName(RoleName.ROLE_USER);
        verify(repository).save(user);
        verify(converter).toEntity(dto);
        verify(converter).toResponse(user);
    }

    @Test
    void create_shouldThrowConflict_whenEmailExists() {

        UserRequestDTO dto =
                new UserRequestDTO(
                        "User test",
                        EMAIL,
                        "12345",
                        List.of(),
                        List.of()
                );

        when(repository.existsByEmail(EMAIL)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.create(dto));

        verify(repository).existsByEmail(EMAIL);
        verifyNoInteractions(converter);
    }

    @Test
    void create_shouldThrowResourceNotFound_whenRoleNotFound() {

        UserRequestDTO dto = new UserRequestDTO(
                "User test",
                EMAIL,
                "12345",
                List.of(),
                List.of()
        );

        when(repository.existsByEmail(EMAIL)).thenReturn(false);
        when(converter.toEntity(dto)).thenReturn(user);
        when(encoder.encode("12345")).thenReturn("encoded");
        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.create(dto));

        verify(repository).existsByEmail(EMAIL);
        verify(converter).toEntity(dto);
        verify(encoder).encode("12345");
        verify(roleRepository).findByName(RoleName.ROLE_USER);
    }


}
