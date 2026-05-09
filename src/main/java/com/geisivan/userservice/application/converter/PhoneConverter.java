package com.geisivan.userservice.application.converter;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.domain.entity.Phone;
import org.springframework.stereotype.Component;

@Component
public class PhoneConverter {

    public Phone toEntity(PhoneRequestDTO dto){
        return Phone.builder()
                .ddd(dto.ddd())
                .number(dto.number())
                .build();
    }

    public PhoneResponseDTO toResponse(Phone entity){
        return new PhoneResponseDTO(
                entity.getId(),
                entity.getDdd(),
                entity.getNumber()
        );
    }
}
