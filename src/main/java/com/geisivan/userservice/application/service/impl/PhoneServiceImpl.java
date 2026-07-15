package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.request.PhoneUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.application.mapper.PhoneMapper;
import com.geisivan.userservice.application.service.CurrentUserService;
import com.geisivan.userservice.application.service.PhoneService;
import com.geisivan.userservice.domain.entity.Phone;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhoneServiceImpl implements PhoneService {

    private final CurrentUserService currentUserService;
    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;

    @Transactional
    @Override
    public PhoneResponseDTO createAuthenticatedUserPhone(PhoneRequestDTO dto) {

        User user = currentUserService.getAuthenticatedUser();

        Phone phone = phoneMapper.toEntity(dto);
        phone.setUser(user);

        return phoneMapper.toDTO(phoneRepository.save(phone));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PhoneResponseDTO> findAuthenticatedUserPhones() {

        User user = currentUserService.getAuthenticatedUser();

        return user.getPhones().stream()
                .map(phoneMapper::toDTO).toList();
    }

    @Transactional
    @Override
    public PhoneResponseDTO updateAuthenticatedUserPhone(
            Long phoneId, PhoneUpdateRequestDTO dto) {

        User user = currentUserService.getAuthenticatedUser();

        Phone phone = getPhoneByIdAndUserId(phoneId, user.getId());
        phone.setUser(user);

        phoneMapper.update(dto, phone);

        return phoneMapper.toDTO(phoneRepository.save(phone));
    }

    @Transactional
    @Override
    public void deleteAuthenticatedUserPhone(Long phoneId) {

        User user = currentUserService.getAuthenticatedUser();

        Phone phone = getPhoneByIdAndUserId(phoneId, user.getId());

        phoneRepository.delete(phone);
    }


    private Phone getPhoneByIdAndUserId(Long phoneId, Long userId) {

        return phoneRepository.findByIdAndUserId(phoneId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phone not found for the authenticated user."));

    }
}
