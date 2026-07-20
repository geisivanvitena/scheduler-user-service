package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRoleUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserStatusUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
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
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) RoleName role,
            Pageable pageable) {

        return ResponseEntity.ok(
                adminUserService.findAllUsers(status, role, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(adminUserService.findUserById(id));
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDTO> findUserByEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(adminUserService.findUserByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO dto) {

        return ResponseEntity.ok(adminUserService.updateUser(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequestDTO dto) {

        return ResponseEntity.ok(adminUserService.updateUserStatus(id, dto));
    }

    @PatchMapping("/{id}/roles")
    public ResponseEntity<UserResponseDTO> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateRequestDTO dto) {

        return ResponseEntity.ok(adminUserService.updateUserRole(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Valid @PathVariable Long id) {

        adminUserService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
