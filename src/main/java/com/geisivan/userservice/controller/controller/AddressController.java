package com.geisivan.userservice.controller.controller;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.request.AddressUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.application.service.AddressService;
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
@RequestMapping("/api/v1/users/me/addresses")
@RequiredArgsConstructor
@Tag(
        name = "Addresses",
        description = "Endpoints for authenticated user address management"
)
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(
            summary = "Create address",
            description = "Creates a new address for the authenticated user"
    )
    @ApiResponses(value = {

            @ApiResponse(responseCode = "201", description = "Address created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AddressResponseDTO> createAddress(
            @Valid @RequestBody AddressRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAuthenticatedUserAddress(dto));
    }

    @GetMapping
    @Operation(
            summary = "Find authenticated user addresses",
            description = "Returns all addresses belonging to the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addresses found successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "404", description = "Addresses not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AddressResponseDTO>> findAuthenticatedUserAddresses() {

        return ResponseEntity.ok(
                addressService.findAuthenticatedUserAddresses());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update address",
            description = "Updates an address belonging to the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AddressResponseDTO> updateAuthenticatedUserAddress(
            @PathVariable("id") Long id,
            @Valid @RequestBody AddressUpdateRequestDTO dto) {

        return ResponseEntity.ok(
                addressService.updateAuthenticatedUserAddress(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete address",
            description = "Deletes an address belonging to the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteAuthenticatedUserAddress(
            @PathVariable("id") Long id) {

        addressService.deleteAuthenticatedUserAddress(id);

        return ResponseEntity.noContent().build();
    }
}
