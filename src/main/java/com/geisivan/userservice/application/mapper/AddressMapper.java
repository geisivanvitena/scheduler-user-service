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

    public void update(AddressRequestDTO dto, Address address) {

        if (dto.street() != null && dto.street().isBlank()){
            address.setStreet(dto.street());
        }
        if (dto.number() != null && dto.number().isBlank()) {
            address.setNumber(dto.number());
        }
        if (dto.neighborhood() != null && dto.neighborhood().isBlank()) {
            address.setNeighborhood(dto.neighborhood());
        }
        if (dto.city() != null && dto.city().isBlank()) {
            address.setCity(dto.city());
        }
        if (dto.state() != null && dto.state().isBlank()) {
            address.setState(dto.state());
        }
        if (dto.postalCode() != null && dto.postalCode().isBlank()) {
            address.setPostalCode(dto.postalCode());
        }
    }
}
