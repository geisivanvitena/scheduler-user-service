package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.LoginRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.LoginResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(
            @Valid @RequestBody UserRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        return ResponseEntity.ok(
                service.authenticate(dto));
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(service.getUserByEmail(email));
    }
}
