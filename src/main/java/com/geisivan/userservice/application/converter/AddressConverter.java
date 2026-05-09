package com.geisivan.userservice.application.converter;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.domain.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressConverter {

    public Address toEntity(AddressRequestDTO dto){
        return Address.builder()
                .street(dto.street())
                .number(dto.number())
                .complement(dto.complement())
                .neighborhood(dto.neighborhood())
                .city(dto.city())
                .state(dto.state())
                .zip(dto.zip())
                .build();
    }

    public AddressResponseDTO toResponse(Address entity){
        return new AddressResponseDTO(
                entity.getId(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getNeighborhood(),
                entity.getCity(),
                entity.getState(),
                entity.getZip()
        );
    }
}
