package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.response.RoleResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.AdminUserMapper;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.impl.AdminUserServiceImpl;
import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ConflictException;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private AdminUserMapper adminUserMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AdminUserServiceImpl adminUserServiceImpl;

    private User user;
    private Role role;

    private static final String EMAIL = "admin@gmail.com";
    private static final String PASSWORD = "123456";

    @BeforeEach
    void setUp() {

        role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
        role.setDescription("Administrator role");


        user = new User();
        user.setId(1L);
        user.setName("Admin Test");
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        user.setRoles(new HashSet<>());
        user.setAddresses(new ArrayList<>());
        user.setPhones(new ArrayList<>());
    }

    @Test
    void createUser_shouldReturnUser_whenRequestIsValid() {

        AdminUserRequestDTO dto =
                new AdminUserRequestDTO(
                        "Admin Test",
                        EMAIL,
                        PASSWORD,
                        Set.of(RoleName.ROLE_ADMIN),
                        UserStatus.ACTIVE,
                        List.of(),
                        List.of()
                );

        RoleResponseDTO roleResponse =
                new RoleResponseDTO(
                        1L,
                        RoleName.ROLE_ADMIN,
                        "Administrator role"
                );

        UserResponseDTO response =
                new UserResponseDTO(
                        1L,
                        "Admin Test",
                        EMAIL,
                        UserStatus.ACTIVE,
                        Set.of(roleResponse),
                        List.of(),
                        List.of(),
                        Instant.now(),
                        null
                );

        when(userRepository.existsByEmail(EMAIL))
                .thenReturn(false);

        when(adminUserMapper.toEntity(dto))
                .thenReturn(user);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn("encodedPassword");

        when(roleRepository.findByName(RoleName.ROLE_ADMIN))
                .thenReturn(Optional.of(role));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toDTO(user))
                .thenReturn(response);

        var result = adminUserServiceImpl.createUser(dto);

        assertNotNull(result);
        assertEquals(EMAIL, result.email());

        verify(userRepository).existsByEmail(EMAIL);
        verify(adminUserMapper).toEntity(dto);
        verify(passwordEncoder).encode(PASSWORD);
        verify(roleRepository).findByName(RoleName.ROLE_ADMIN);
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    void createUser_shouldThrowConflictException_whenEmailExists() {

        AdminUserRequestDTO dto =
                new AdminUserRequestDTO(
                        "Admin Test",
                        EMAIL,
                        PASSWORD,
                        Set.of(RoleName.ROLE_ADMIN),
                        UserStatus.ACTIVE,
                        List.of(),
                        List.of()
                );

        when(userRepository.existsByEmail(EMAIL))
                .thenReturn(true);

        assertThrows(ConflictException.class,
                () -> adminUserServiceImpl.createUser(dto));

        verify(userRepository).existsByEmail(EMAIL);
        verifyNoInteractions(adminUserMapper);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(roleRepository);
    }

    @Test
    void createUser_shouldThrowResourceNotFoundException_whenRoleDoesNotExist() {

        AdminUserRequestDTO dto =
                new AdminUserRequestDTO(
                        "Admin Test",
                        EMAIL,
                        PASSWORD,
                        Set.of(RoleName.ROLE_ADMIN),
                        UserStatus.ACTIVE,
                        List.of(),
                        List.of()
                );

        when(userRepository.existsByEmail(EMAIL))
                .thenReturn(false);

        when(adminUserMapper.toEntity(dto))
                .thenReturn(user);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn("encodedPassword");

        when(roleRepository.findByName(RoleName.ROLE_ADMIN))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adminUserServiceImpl.createUser(dto));

        verify(userRepository).existsByEmail(EMAIL);
        verify(adminUserMapper).toEntity(dto);
        verify(passwordEncoder).encode(PASSWORD);
        verify(roleRepository).findByName(RoleName.ROLE_ADMIN);
        verify(userRepository, never()).save(any());
    }

    @Test
    void findAllUsers_shouldReturnPageOfUsers() {

        Pageable pageable = PageRequest.of(0, 10);

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

        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        when(userMapper.toDTO(user))
                .thenReturn(response);

        Page<UserResponseDTO> result =
                adminUserServiceImpl.findAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(EMAIL, result.getContent().get(0).email());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toDTO(user);
    }

    @Test
    void findAllUsers_shouldReturnEmptyPage_whenUsersDoNotExist() {

        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable))
                .thenReturn(Page.empty());

        Page<UserResponseDTO> result =
                adminUserServiceImpl.findAllUsers(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll(pageable);
        verifyNoInteractions(userMapper);
    }
}
