package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> findAuthenticatedUser(){
        return ResponseEntity.ok(
                userService.findAuthenticatedUser());
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> updateAuthenticatedUser(
            @Valid @RequestBody UserUpdateRequestDTO dto){

        return ResponseEntity.ok(
                userService.updateAuthenticatedUser(dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAuthenticatedUser(){

        userService.deleteAuthenticatedUser();

        return ResponseEntity.noContent().build();
    }
}
