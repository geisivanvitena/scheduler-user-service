package com.geisivan.userservice;

import com.geisivan.userservice.application.converter.UserConverter;
import com.geisivan.userservice.application.dto.request.LoginRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.UserService;
import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.infrastructure.exception.ConflictException;
import com.geisivan.userservice.infrastructure.exception.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.exception.UnauthorizedException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.auth.UserPrincipal;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
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
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

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

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        EMAIL,
                        "12345"
                );


        UserPrincipal principal = mock(UserPrincipal.class);

        when(principal.id()).thenReturn(1L);
        when(principal.email()).thenReturn(EMAIL);

        Authentication auth = mock(Authentication.class);

        when(auth.getPrincipal()).thenReturn(principal);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(auth);

        when(jwtUtil.generateToken(1L, EMAIL))
                .thenReturn("token");

        var response = service.authenticate(dto);

        assertNotNull(response);
        assertEquals("token", response.token());
        assertEquals(EMAIL, response.email());
        assertEquals(1L, response.userId());

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(jwtUtil).generateToken(1L, EMAIL);
    }

    @Test
    void login_shouldThrowException_whenCredentialsAreInvalid() {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        EMAIL,
                        "wrong"
                );

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid"));

        assertThrows(UnauthorizedException.class,
                () -> service.authenticate(dto));

        verify(authenticationManager).authenticate(any());
    }
}
