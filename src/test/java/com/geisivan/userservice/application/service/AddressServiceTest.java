package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.request.AddressUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.application.mapper.AddressMapper;
import com.geisivan.userservice.application.service.impl.AddressServiceImpl;
import com.geisivan.userservice.domain.entity.Address;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressServiceImpl;

    private User user;

    private Address address;

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

        address = new Address();

        address.setId(1L);
        address.setStreet("Main Street");
        address.setNumber("123");
        address.setNeighborhood("Downtown");
        address.setCity("New York");
        address.setState("NY");
        address.setPostalCode("10001-000");
    }

    @Test
    void createAuthenticatedUserAddress_shouldCreateAddress_whenUserIsAuthenticated() {

        AddressRequestDTO request =
                new AddressRequestDTO(
                        "Main Street",
                        "123",
                        "Downtown",
                        "New York",
                        "NY",
                        "10001-000"
                );

        AddressResponseDTO response =
                new AddressResponseDTO(
                        1L,
                        "Main Street",
                        "123",
                        "Downtown",
                        "New York",
                        "NY",
                        "10001-000"
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(addressMapper.toEntity(request))
                .thenReturn(address);

        when(addressRepository.save(address))
                .thenReturn(address);

        when(addressMapper.toDTO(address))
                .thenReturn(response);

        var result =
                addressServiceImpl.createAuthenticatedUserAddress(request);

        assertNotNull(result);
        assertEquals("Main Street", result.street());
        assertEquals("New York", result.city());
        assertEquals(user, address.getUser());

        verify(currentUserService).getAuthenticatedUser();
        verify(addressMapper).toEntity(request);
        verify(addressRepository).save(address);
        verify(addressMapper).toDTO(address);
    }

    @Test
    void findAuthenticatedUserAddresses_shouldReturnAllAddresses_whenUserHasAddresses() {

        Address secondAddress = new Address();

        secondAddress.setId(2L);
        secondAddress.setStreet("Oak Avenue");
        secondAddress.setNumber("456");
        secondAddress.setNeighborhood("West Side");
        secondAddress.setCity("Los Angeles");
        secondAddress.setState("CA");
        secondAddress.setPostalCode("90001-000");

        user.getAddresses().add(address);
        user.getAddresses().add(secondAddress);

        AddressResponseDTO firstResponse =
                new AddressResponseDTO(
                        1L,
                        "Main Street",
                        "123",
                        "Downtown",
                        "New York",
                        "NY",
                        "10001-000"
                );

        AddressResponseDTO secondResponse =
                new AddressResponseDTO(
                        2L,
                        "Oak Avenue",
                        "456",
                        "West Side",
                        "Los Angeles",
                        "CA",
                        "90001-000"
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(addressMapper.toDTO(address))
                .thenReturn(firstResponse);

        when(addressMapper.toDTO(secondAddress))
                .thenReturn(secondResponse);

        var result =
                addressServiceImpl.findAuthenticatedUserAddresses();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Main Street", result.get(0).street());
        assertEquals("Oak Avenue", result.get(1).street());

        verify(currentUserService).getAuthenticatedUser();
        verify(addressMapper).toDTO(address);
        verify(addressMapper).toDTO(secondAddress);
    }

    @Test
    void updateAuthenticatedUserAddress_shouldUpdateAddress_whenAddressExists() {

        Long addressId = 1L;

        AddressUpdateRequestDTO request =
                new AddressUpdateRequestDTO(
                        "Ocean Drive",
                        "1500",
                        "South Beach",
                        "Miami",
                        "FL",
                        "33139-000"
                );

        AddressResponseDTO response =
                new AddressResponseDTO(
                        1L,
                        "Ocean Drive",
                        "1500",
                        "South Beach",
                        "Miami",
                        "FL",
                        "33139-000"
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(addressRepository.findByIdAndUserId(addressId, user.getId()))
                .thenReturn(Optional.of(address));

        when(addressRepository.save(address))
                .thenReturn(address);

        when(addressMapper.toDTO(address))
                .thenReturn(response);

        var result =
                addressServiceImpl.updateAuthenticatedUserAddress(
                        addressId,
                        request);

        assertNotNull(result);
        assertEquals("Ocean Drive", result.street());
        assertEquals("Miami", result.city());

        verify(currentUserService).getAuthenticatedUser();
        verify(addressRepository).findByIdAndUserId(addressId, user.getId());
        verify(addressMapper).update(request, address);
        verify(addressRepository).save(address);
        verify(addressMapper).toDTO(address);
    }

    @Test
    void updateAuthenticatedUserAddress_shouldThrowException_whenAddressDoesNotExist() {

        Long addressId = 99L;

        AddressUpdateRequestDTO request =
                new AddressUpdateRequestDTO(
                        "Ocean Drive",
                        "1500",
                        "South Beach",
                        "Miami",
                        "FL",
                        "33139-000"
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(addressRepository.findByIdAndUserId(addressId, user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressServiceImpl.updateAuthenticatedUserAddress(
                        addressId,
                        request));

        verify(currentUserService).getAuthenticatedUser();
        verify(addressRepository).findByIdAndUserId(addressId, user.getId());
        verify(addressMapper, never()).update(any(), any());
        verify(addressRepository, never()).save(any());
    }

    @Test
    void deleteAuthenticatedUserAddress_shouldDeleteAddress_whenAddressExists() {

        Long addressId = 1L;

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(addressRepository.findByIdAndUserId(
                addressId,
                user.getId()))
                .thenReturn(Optional.of(address));

        addressServiceImpl.deleteAuthenticatedUserAddress(addressId);

        verify(currentUserService).getAuthenticatedUser();
        verify(addressRepository).findByIdAndUserId(addressId, user.getId());
        verify(addressRepository).delete(address);
    }

    @Test
    void deleteAuthenticatedUserAddress_shouldThrowException_whenAddressDoesNotExist() {

        Long addressId = 99L;

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(addressRepository.findByIdAndUserId(
                addressId,
                user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressServiceImpl.deleteAuthenticatedUserAddress(addressId));

        verify(currentUserService).getAuthenticatedUser();
        verify(addressRepository).findByIdAndUserId(addressId, user.getId());
        verify(addressRepository, never()).delete(any());
    }
}
