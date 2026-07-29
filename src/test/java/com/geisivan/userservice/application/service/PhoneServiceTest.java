package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.request.PhoneUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.application.mapper.PhoneMapper;
import com.geisivan.userservice.application.service.impl.PhoneServiceImpl;
import com.geisivan.userservice.domain.entity.Phone;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.PhoneType;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.PhoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PhoneServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private PhoneRepository phoneRepository;

    @Mock
    private PhoneMapper phoneMapper;

    @InjectMocks
    private PhoneServiceImpl phoneServiceImpl;

    private User user;

    private Phone phone;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(1L);
        user.setName("User Test");
        user.setEmail("teste@gmail.com");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of());
        user.setAddresses(new ArrayList<>());
        user.setPhones(new ArrayList<>());

        phone = new Phone();

        phone.setId(1L);
        phone.setPhoneNumber("123456789");
        phone.setUser(user);
    }

    @Test
    void createAuthenticatedUserPhone_shouldCreateAddress_whenUserIsAuthenticated() {

        PhoneRequestDTO request = new PhoneRequestDTO(
                "71",
                "999999999",
                PhoneType.MOBILE
        );

        PhoneResponseDTO response = new PhoneResponseDTO(
                1L,
                "71",
                "999999999",
                PhoneType.MOBILE
        );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(phoneMapper.toEntity(request))
                .thenReturn(phone);

        when(phoneRepository.save(phone))
                .thenReturn(phone);

        when(phoneMapper.toDTO(phone))
                .thenReturn(response);

        var result =
                phoneServiceImpl.createAuthenticatedUserPhone(request);

        assertNotNull(result);
        assertEquals("71", result.areaCode());
        assertEquals("999999999", result.phoneNumber());
        assertEquals(PhoneType.MOBILE, result.phoneType());
        assertEquals(user, phone.getUser());

        verify(currentUserService).getAuthenticatedUser();
        verify(phoneMapper).toEntity(request);
        verify(phoneRepository).save(phone);
        verify(phoneMapper).toDTO(phone);
    }

    @Test
    void findAuthenticatedUserPhones_shouldReturnAllPhones_whenUserHasPhones() {

        Phone secondPhone = new Phone();

        secondPhone.setId(2L);
        secondPhone.setAreaCode("41");
        secondPhone.setPhoneNumber("992378825");
        secondPhone.setPhoneType(PhoneType.WORK);

        user.getPhones().add(phone);
        user.getPhones().add(secondPhone);

        PhoneResponseDTO firstResponse =
                new PhoneResponseDTO(
                        1L,
                        "71",
                        "999887766",
                        PhoneType.MOBILE
                );

        PhoneResponseDTO secondResponse =
                new PhoneResponseDTO(
                        2L,
                        "41",
                        "992378825",
                        PhoneType.WORK
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(phoneMapper.toDTO(phone))
                .thenReturn(firstResponse);

        when(phoneMapper.toDTO(secondPhone))
                .thenReturn(secondResponse);

        var result =
                phoneServiceImpl.findAuthenticatedUserPhones();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("71", result.get(0).areaCode());
        assertEquals("41", result.get(1).areaCode());

        verify(currentUserService).getAuthenticatedUser();
        verify(phoneMapper).toDTO(phone);
        verify(phoneMapper).toDTO(secondPhone);
    }

    @Test
    void updateAuthenticatedUserPhone_shouldUpdatePhone_whenPhoneExists() {

        Long phoneId = 1L;

        PhoneUpdateRequestDTO request =
                new PhoneUpdateRequestDTO(
                        "71",
                        "999999999",
                        PhoneType.OTHER
                );

        PhoneResponseDTO response =
                new PhoneResponseDTO(
                        1L,
                        "71",
                        "999999999",
                        PhoneType.OTHER
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(phoneRepository.findByIdAndUserId(phoneId, user.getId()))
                .thenReturn(Optional.of(phone));

        when(phoneRepository.save(phone)).thenReturn(phone);

        when(phoneMapper.toDTO(phone)).thenReturn(response);

        var result = phoneServiceImpl.updateAuthenticatedUserPhone(
                phoneId, request);

        assertNotNull(result);
        assertEquals("71", result.areaCode());
        assertEquals("999999999", result.phoneNumber());
        assertEquals(PhoneType.OTHER, result.phoneType());

        verify(currentUserService).getAuthenticatedUser();
        verify(phoneRepository).findByIdAndUserId(phoneId, user.getId());
        verify(phoneMapper).update(request, phone);
        verify(phoneRepository).save(phone);
        verify(phoneMapper).toDTO(phone);
    }

    @Test
    void updateAuthenticatedUserPhone_shouldThrowException_whenPhoneDoesNotExist() {

        Long phoneId = 99L;

        PhoneUpdateRequestDTO request =
                new PhoneUpdateRequestDTO(
                        "71",
                        "999999999",
                        PhoneType.OTHER
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(phoneRepository.findByIdAndUserId(phoneId, user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> phoneServiceImpl.updateAuthenticatedUserPhone(
                        phoneId, request));

        verify(currentUserService).getAuthenticatedUser();
        verify(phoneRepository).findByIdAndUserId(phoneId, user.getId());
        verify(phoneMapper, never()).update(any(), any());
        verify(phoneRepository, never()).save(any());
    }

    @Test
    void deleteAuthenticatedUserPhone_shouldDeletePhone_whenPhoneExists() {

        Long phoneId = 1L;

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(phoneRepository.findByIdAndUserId( phoneId, user.getId()))
                .thenReturn(Optional.of(phone));

        phoneServiceImpl.deleteAuthenticatedUserPhone(phoneId);

        verify(currentUserService).getAuthenticatedUser();
        verify(phoneRepository).findByIdAndUserId(phoneId, user.getId());
        verify(phoneRepository).delete(phone);
    }

    @Test
    void deleteAuthenticatedUserPhone_shouldThrowException_whenPhoneDoesNotExist() {

        Long phoneId = 99L;

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(phoneRepository.findByIdAndUserId( phoneId, user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> phoneServiceImpl.deleteAuthenticatedUserPhone(phoneId));

        verify(currentUserService).getAuthenticatedUser();
        verify(phoneRepository).findByIdAndUserId(phoneId, user.getId());
        verify(phoneRepository, never()).delete(any());
    }
}
