package com.geisivan.userservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PhoneRequestDTO (

    @NotBlank(message = "Area code is required")
    @Pattern(regexp = "\\d{2}", message = "Area code must have 2 digits")
    String ddd,

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{8,9}", message = "Phone number must have 8 or 9 digits")
    @Size(max = 9, message = "Phone number must have at most 9 characters")
    String number
){}
