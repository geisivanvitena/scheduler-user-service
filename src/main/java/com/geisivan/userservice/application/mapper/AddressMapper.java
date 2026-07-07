package com.geisivan.userservice.application.mapper;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.domain.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequestDTO dto) {
        return Address.builder()
                .street(dto.street())
                .number(dto.number())
                .neighborhood(dto.neighborhood())
                .city(dto.city())
                .state(dto.state())
                .postalCode(dto.postalCode())
                .build();
    }

    public AddressResponseDTO toDTO(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getPostalCode()
        );
    }
}
