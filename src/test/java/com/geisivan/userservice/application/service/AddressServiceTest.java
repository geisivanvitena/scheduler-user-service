package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.application.mapper.AddressMapper;
import com.geisivan.userservice.application.service.impl.AddressServiceImpl;
import com.geisivan.userservice.domain.entity.Address;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
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
    void createAddress_shouldCreateAddress_whenUserIsAuthenticated() {

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
}
