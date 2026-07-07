package com.geisivan.userservice.application.mapper;

import com.geisivan.userservice.application.dto.response.RoleResponseDTO;
import com.geisivan.userservice.domain.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponseDTO toDTO(Role entity) {
        return new RoleResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}
