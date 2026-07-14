package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.application.mapper.PhoneMapper;
import com.geisivan.userservice.application.service.CurrentUserService;
import com.geisivan.userservice.application.service.PhoneService;
import com.geisivan.userservice.domain.entity.Phone;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneServiceImpl implements PhoneService {

    private final CurrentUserService currentUserService;
    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;

    @Override
    public PhoneResponseDTO createAuthenticatedUserPhone(PhoneRequestDTO dto) {

        User user = currentUserService.getAuthenticatedUser();

        Phone phone = phoneMapper.toEntity(dto);
        phone.setUser(user);

        return phoneMapper.toDTO(phoneRepository.save(phone));
    }
}
