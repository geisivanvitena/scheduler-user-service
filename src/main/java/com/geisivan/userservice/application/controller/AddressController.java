package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.request.AddressUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.application.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(
            @Valid @RequestBody AddressRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAuthenticatedUserAddress(dto));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> findAuthenticatedUserAddresses() {

        return ResponseEntity.ok(
                addressService.findAuthenticatedUserAddresses());
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAuthenticatedUserAddress(
            @PathVariable("addressId") Long addressId,
            @Valid @RequestBody AddressUpdateRequestDTO dto) {

        return ResponseEntity.ok(
                addressService.updateAuthenticatedUserAddress(addressId, dto));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAuthenticatedUserAddress(
            @PathVariable("addressId") Long addressId) {

        addressService.deleteAuthenticatedUserAddress(addressId);

        return ResponseEntity.noContent().build();
    }
}
