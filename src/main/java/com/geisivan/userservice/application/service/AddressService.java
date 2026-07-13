package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;

public interface AddressService {

    AddressResponseDTO createAddress(AddressRequestDTO dto);
}
