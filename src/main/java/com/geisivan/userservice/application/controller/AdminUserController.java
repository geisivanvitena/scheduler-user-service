package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.response.PageResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.AdminUserService;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody AdminUserRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminUserService.createUser(dto));
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> findAllUsers(
            @RequestParam(required = false) UserStatus  status,
            @RequestParam(required = false) RoleName role,
            Pageable pageable){

        return ResponseEntity.ok(
                adminUserService.findAllUsers(status, role, pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> findUserById(
            @PathVariable Long userId) {

        return ResponseEntity.ok(adminUserService.findUserById(userId));
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDTO> findUserByEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(adminUserService.findUserByEmail(email));
    }
}
