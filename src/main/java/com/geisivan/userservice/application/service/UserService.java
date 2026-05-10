package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.converter.UserConverter;
import com.geisivan.userservice.application.dto.request.LoginRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.LoginResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserConverter converter;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponseDTO create(UserRequestDTO dto){
        validateEmailExists(dto.email());

        User user = buildUser(dto);
        user = repository.save(user);

        return converter.toResponse(user);
    }

    public LoginResponseDTO authenticate(LoginRequestDTO dto){
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    dto.email(),
                                    dto.password())
                    );

            UserPrincipal user =  (UserPrincipal) authentication.getPrincipal();
            String token = jwtUtil.generateToken(
                    user.id(),
                    user.email()
            );

            return new LoginResponseDTO(
                    token,
                    user.id(),
                    user.email()
            );

        }catch (AuthenticationException e) {
            throw new UnauthorizedException(
                    "Invalid username or password");
        }
    }

    public UserResponseDTO getUserByEmail(String email){
        User user = repository.findByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found"));

        return converter.toResponse(user);
    }

    private User buildUser(UserRequestDTO dto){
        User user = converter.toEntity(dto);
        user.setPassword(encoder.encode(dto.password()));

        Role role = findRole(RoleName.ROLE_USER);
        user.getRoles().add(role);

        return user;
    }

    private Role findRole(RoleName role) {
        return roleRepository.findByName(role)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role " + role + " not found"));
    }

    private void validateEmailExists(String email){
        if (repository.existsByEmail(email)){
            throw new ConflictException(
                    "Email already exists: " + email);
        }
    }
}
