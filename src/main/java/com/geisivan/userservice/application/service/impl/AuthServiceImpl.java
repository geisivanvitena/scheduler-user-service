package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.LoginRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.LoginResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.AuthService;
import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.infrastructure.exception.custom.ConflictException;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.exception.custom.UserUnauthorizedException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.auth.MainUser;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponseDTO register(UserRequestDTO dto) {

        validateEmailExists(dto.email());

        User user = buildUser(dto);

        user = userRepository.save(user);

        return userMapper.toDTO(user);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.email(),
                            dto.password()));

            var mainUser = (MainUser) authentication.getPrincipal();

            String token = "Bearer " + jwtUtil.generateToken(
                    mainUser.id(),
                    mainUser.email());

            return new LoginResponseDTO(
                    token,
                    "Bearer",
                    mainUser.id(),
                    mainUser.email()
            );

        }catch (AuthenticationException e) {

            throw new UserUnauthorizedException(
                    "Invalid email or password");
        }
    }

    private User buildUser(UserRequestDTO dto) {

        User user = userMapper.toEntity(dto);

        user.setPassword(passwordEncoder.encode(dto.password()));

        Role role = getDefaultRole();
        user.getRoles().add(role);

        return user;
    }

    private Role getDefaultRole() {

        return roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Default role not found"));
    }

    private void validateEmailExists(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already exists: " + email);
        }
    }
}
