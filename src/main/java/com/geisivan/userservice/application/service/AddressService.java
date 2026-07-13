package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import java.util.List;

public interface AddressService {

    AddressResponseDTO createAuthenticatedUserAddress(AddressRequestDTO dto);

    List<AddressResponseDTO> findAuthenticatedUserAddress();
}
