package com.geisivan.userservice.application.dto.request;

import com.geisivan.userservice.domain.enums.PhoneType;
import jakarta.validation.constraints.Pattern;

public record PhoneUpdateRequestDTO(

        @Pattern(
                regexp = "\\d{2}",
                message = "Area code must have 2 digits")
        String areaCode,

        @Pattern(
                regexp = "\\d{8,9}",
                message = "Phone number must have 8 or 9 digits")
        String phoneNumber,

        PhoneType phoneType
) {}
