package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.request.PhoneUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.application.service.PhoneService;
import com.geisivan.userservice.infrastructure.security.config.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/phones")
@RequiredArgsConstructor
@Tag(
        name = "Phones",
        description = "Endpoints for authenticated user phone management"
)
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class PhoneController {

    private final PhoneService phoneService;

    @PostMapping
    @Operation(
            summary = "Create phone",
            description = "Creates a new phone number for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Phone created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PhoneResponseDTO> createAuthenticatedUserPhone(
            @Valid @RequestBody PhoneRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(phoneService.createAuthenticatedUserPhone(dto));
    }

    @GetMapping
    @Operation(
            summary = "Find authenticated user phones",
            description = "Returns all phone numbers belonging to the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phones found successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "404", description = "Phones not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PhoneResponseDTO>> findAuthenticatedUserPhones() {

        return ResponseEntity.ok(
                phoneService.findAuthenticatedUserPhones());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update phone",
            description = "Updates a phone number belonging to the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "404", description = "Phone not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PhoneResponseDTO> updateAuthenticatedUserPhone(
            @PathVariable Long id,
            @Valid @RequestBody PhoneUpdateRequestDTO dto) {

        return ResponseEntity.ok(
                phoneService.updateAuthenticatedUserPhone(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete phone",
            description = "Deletes a phone number belonging to the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Phone deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "404", description = "Phone not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteAuthenticatedUserPhone(
            @PathVariable Long id) {

        phoneService.deleteAuthenticatedUserPhone(id);

        return ResponseEntity.noContent().build();
    }
}
