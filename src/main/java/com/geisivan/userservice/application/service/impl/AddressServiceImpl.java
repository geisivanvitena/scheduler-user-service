package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.application.mapper.AddressMapper;
import com.geisivan.userservice.application.service.AddressService;
import com.geisivan.userservice.application.service.CurrentUserService;
import com.geisivan.userservice.domain.entity.Address;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.repository.AddressRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final CurrentUserService currentUserService;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Transactional
    @Override
    public AddressResponseDTO createAddress(AddressRequestDTO dto) {

        User user = currentUserService.getAuthenticatedUser();

        Address address = addressMapper.toEntity(dto);
        address.setUser(user);

        return addressMapper.toDTO(addressRepository.save(address));
    }
}

