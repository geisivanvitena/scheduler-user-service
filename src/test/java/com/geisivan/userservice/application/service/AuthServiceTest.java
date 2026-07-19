package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.LoginRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.impl.AuthServiceImpl;
import com.geisivan.userservice.application.validator.UserValidator;
import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ConflictException;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.exception.custom.UserUnauthorizedException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.auth.MainUser;
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
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private User user;
    private Role role;

    private static final String EMAIL = "teste@gmail.com";
    private static final String PASSWORD = "123456";

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(2L);
        role.setName(RoleName.ROLE_USER);
        role.setDescription("Standard user role with limited access");

        user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        user.setRoles(new HashSet<>());
        user.setAddresses(new ArrayList<>());
        user.setPhones(new ArrayList<>());
    }

    @Test
    void register_shouldReturnUser_whenRequestIsValid() {
        UserRequestDTO dto =
                new UserRequestDTO(
                        "User test",
                        EMAIL,
                        "123456",
                        List.of(),
                        List.of()
                );

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

        doNothing()
                .when(userValidator)
                .validateEmailExists(EMAIL);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn("encodedPassword");

        when(userMapper.toEntity(dto))
                .thenReturn(user);

        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.of(role));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toDTO(user))
                .thenReturn(response);

        var result = authServiceImpl.register(dto);

        assertNotNull(result);
        assertEquals(EMAIL, result.email());

        verify(userValidator).validateEmailExists(EMAIL);
        verify(passwordEncoder).encode("123456");
        verify(roleRepository).findByName(RoleName.ROLE_USER);
        verify(userRepository).save(user);
        verify(userMapper).toEntity(dto);
        verify(userMapper).toDTO(user);
    }

    @Test
    void register_shouldThrowConflictException_whenEmailExists() {
        UserRequestDTO dto =
                new UserRequestDTO(
                        "User test",
                        EMAIL,
                        "123456",
                        List.of(),
                        List.of()
                );

        doThrow(new ConflictException("Email already exists"))
                .when(userValidator)
                .validateEmailExists(EMAIL);

        assertThrows(ConflictException.class,
                () -> authServiceImpl.register(dto));

        verify(userValidator).validateEmailExists(EMAIL);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(userRepository);
    }

    @Test
    void register_shouldThrowResourceNotFoundException_whenDefaultRoleDoesNotExist() {
        UserRequestDTO dto =
                new UserRequestDTO(
                        "User Test",
                        EMAIL,
                        PASSWORD,
                        List.of(),
                        List.of()
                );

        doNothing()
                .when(userValidator)
                .validateEmailExists(EMAIL);

        when(userMapper.toEntity(dto))
                .thenReturn(user);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn("encodedPassword");

        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authServiceImpl.register(dto));

        verify(userValidator).validateEmailExists(EMAIL);
        verify(userMapper).toEntity(dto);
        verify(passwordEncoder).encode(PASSWORD);
        verify(roleRepository).findByName(RoleName.ROLE_USER);
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        EMAIL,
                        "123456"
                );

        var mainUser = mock(MainUser.class);

        when(mainUser.id()).thenReturn(1L);
        when(mainUser.email()).thenReturn(EMAIL);

        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(mainUser);

        when(jwtUtil.generateToken(1L, EMAIL))
                .thenReturn("token");

        var response  = authServiceImpl.login(dto);

        assertNotNull(response);
        assertEquals("token", response.token());
        assertEquals(EMAIL, response.email());
        assertEquals(1L, response.userId());

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class));

        verify(jwtUtil).generateToken(1L, EMAIL);
    }

    @Test
    void login_shouldThrowUserUnauthorizedException_whenCredentialsAreInvalid() {

        LoginRequestDTO dto =
                new LoginRequestDTO(
                        EMAIL,
                        "wrong"
                );

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid"));

        assertThrows(UserUnauthorizedException.class,
                () -> authServiceImpl.login(dto));

        verify(authenticationManager).authenticate(any());
    }
}
