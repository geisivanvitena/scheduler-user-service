package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.application.service.PhoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/phones")
@RequiredArgsConstructor
public class PhoneController {

    private final PhoneService phoneService;

    @PostMapping
    public ResponseEntity<PhoneResponseDTO> createAuthenticatedUserPhone(
            @Valid @RequestBody PhoneRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(phoneService.createAuthenticatedUserPhone(dto));
    }

    @GetMapping
    public ResponseEntity<List<PhoneResponseDTO>> findAuthenticatedUserPhones() {

        return ResponseEntity.ok(
                phoneService.findAuthenticatedUserPhones());
    }
}
