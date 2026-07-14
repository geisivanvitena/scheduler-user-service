package com.geisivan.userservice.application.mapper;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.request.PhoneUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.domain.entity.Phone;
import org.springframework.stereotype.Component;

@Component
public class PhoneMapper {

    public Phone toEntity (PhoneRequestDTO dto){
        return Phone.builder()
                .areaCode(dto.areaCode())
                .phoneNumber(dto.phoneNumber())
                .phoneType(dto.phoneType())
                .build();
    }

    public PhoneResponseDTO toDTO (Phone entity) {
        return new PhoneResponseDTO(
                entity.getId(),
                entity.getAreaCode(),
                entity.getPhoneNumber(),
                entity.getPhoneType()
        );
    }

    public void update(PhoneUpdateRequestDTO dto, Phone phone) {

        if (dto.areaCode() != null && !dto.areaCode().isBlank()){
            phone.setAreaCode(dto.areaCode());
        }

        if (dto.phoneNumber() != null && !dto.phoneNumber().isBlank()){
            phone.setPhoneNumber(dto.phoneNumber());
        }

        if (dto.phoneType() != null){
            phone.setPhoneType(dto.phoneType());
        }
    }
}
