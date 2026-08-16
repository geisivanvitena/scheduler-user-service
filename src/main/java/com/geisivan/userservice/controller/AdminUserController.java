package com.geisivan.userservice.controller;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRoleUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserStatusUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PageResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.AdminUserService;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.security.config.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(
        name = "Admin Users",
        description = "Administrative endpoints for user management"
)
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    @Operation(
            summary = "Create user",
            description = "Creates a new user account by an administrator"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have admin permission"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody AdminUserRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminUserService.createUser(dto));
    }

    @GetMapping
    @Operation(
            summary = "Find all users",
            description = "Returns a paginated list of users with optional filters"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have admin permission"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> findAllUsers(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) RoleName role,
            Pageable pageable) {

        return ResponseEntity.ok(
                adminUserService.findAllUsers(status, role, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Find user by id",
            description = "Returns a user by identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have admin permission"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> findUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(adminUserService.findUserById(id));
    }

    @GetMapping("/email")
    @Operation(
            summary = "Find user by email",
            description = "Returns a user by email address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have admin permission"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> findUserByEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(adminUserService.findUserByEmail(email));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user",
            description = "Updates user information by administrator"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have admin permission"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO dto) {

        return ResponseEntity.ok(adminUserService.updateUser(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Update user status",
            description = "Updates user account status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have admin permission"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequestDTO dto) {

        return ResponseEntity.ok(adminUserService.updateUserStatus(id, dto));
    }

    @PatchMapping("/{id}/roles")
    @Operation(
            summary = "Update user roles",
            description = "Updates user roles and permissions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User roles updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have admin permission"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDTO> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateRequestDTO dto) {

        return ResponseEntity.ok(adminUserService.updateUserRole(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user",
            description = "Deletes a user account by administrator"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have admin permission"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteUser(
            @Valid @PathVariable Long id) {

        adminUserService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
