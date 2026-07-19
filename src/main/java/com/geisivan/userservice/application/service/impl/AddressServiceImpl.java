package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.request.AddressUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.application.mapper.AddressMapper;
import com.geisivan.userservice.application.service.AddressService;
import com.geisivan.userservice.application.service.CurrentUserService;
import com.geisivan.userservice.domain.entity.Address;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.AddressRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final CurrentUserService currentUserService;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Transactional
    @Override
    public AddressResponseDTO createAuthenticatedUserAddress(AddressRequestDTO dto) {

        User user = currentUserService.getAuthenticatedUser();

        Address address = addressMapper.toEntity(dto);
        address.setUser(user);

        return addressMapper.toDTO(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AddressResponseDTO> findAuthenticatedUserAddresses() {

        User user = currentUserService.getAuthenticatedUser();

        return user.getAddresses().stream()
                .map(addressMapper::toDTO).toList();
    }

    @Transactional
    @Override
    public AddressResponseDTO updateAuthenticatedUserAddress(
            Long id, AddressUpdateRequestDTO dto) {

        User user = currentUserService.getAuthenticatedUser();

        Address address = getAddressByIdAndUserId(id, user.getId());

        addressMapper.update(dto, address);

        return addressMapper.toDTO(addressRepository.save(address));
    }

    @Transactional
    @Override
    public void deleteAuthenticatedUserAddress(Long id) {

        User user = currentUserService.getAuthenticatedUser();

        Address address = getAddressByIdAndUserId(id, user.getId());

        addressRepository.delete(address);
    }

    private Address getAddressByIdAndUserId(Long id, Long userId) {

        return addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found for the authenticated user."));
    }
}

