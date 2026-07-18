package com.geisivan.userservice.application.dto.response;


import java.util.List;

public record PageResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
){}
