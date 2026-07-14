package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.request.AddressUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.application.service.AddressService;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import com.geisivan.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private AddressService addressService;

    private static final String BASE_URL = "/api/v1/users/me/addresses";

    private static final String VALID_ADDRESS_REQUEST = """
        {
            "street": "Main Street",
            "number": "123",
            "neighborhood": "Downtown",
            "city": "New York",
            "state": "NY",
            "postalCode": "10001-000"
        }
        """;

    private static final String INVALID_ADDRESS_REQUEST = """
    {
        "street": "",
        "number": "",
        "neighborhood": "",
        "city": "",
        "state": "NEW",
        "postalCode": "123"
    }
    """;

    private static final String VALID_UPDATE_ADDRESS_REQUEST = """
{
    "street": "Ocean Drive",
    "number": "1500",
    "neighborhood": "South Beach",
    "city": "Miami",
    "state": "FL",
    "postalCode": "33139-000"
}
""";

    private static final String INVALID_UPDATE_ADDRESS_REQUEST = """
{
    "street": "",
    "number": "",
    "neighborhood": "",
    "city": "",
    "state": "FLORIDA",
    "postalCode": "123"
}
""";

    @Test
    void createAuthenticatedUserAddress_shouldReturn201_whenRequestIsValid()
            throws Exception {

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

        when(addressService.createAuthenticatedUserAddress(any(AddressRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ADDRESS_REQUEST))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id")
                        .value(1))

                .andExpect(jsonPath("$.street")
                        .value("Main Street"))

                .andExpect(jsonPath("$.number")
                        .value("123"))

                .andExpect(jsonPath("$.neighborhood")
                        .value("Downtown"))

                .andExpect(jsonPath("$.city")
                        .value("New York"))

                .andExpect(jsonPath("$.state")
                        .value("NY"))

                .andExpect(jsonPath("$.postalCode")
                        .value("10001-000"));

        verify(addressService)
                .createAuthenticatedUserAddress(any(AddressRequestDTO.class));
    }

    @Test
    void createAuthenticatedUserAddress_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_ADDRESS_REQUEST))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(addressService);
    }

    @Test
    void findAuthenticatedUserAddresses_shouldReturn200_whenAddressesExist()
            throws Exception {

        AddressResponseDTO firstAddress =
                new AddressResponseDTO(
                        1L,
                        "Main Street",
                        "123",
                        "Downtown",
                        "New York",
                        "NY",
                        "10001-000"
                );

        AddressResponseDTO secondAddress =
                new AddressResponseDTO(
                        2L,
                        "Oak Avenue",
                        "456",
                        "West Side",
                        "Los Angeles",
                        "CA",
                        "90001-000"
                );

        when(addressService.findAuthenticatedUserAddresses())
                .thenReturn(List.of(firstAddress, secondAddress));

        mockMvc.perform(get(BASE_URL))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()")
                        .value(2))

                .andExpect(jsonPath("$[0].id")
                        .value(1))

                .andExpect(jsonPath("$[0].street")
                        .value("Main Street"))

                .andExpect(jsonPath("$[1].id")
                        .value(2))

                .andExpect(jsonPath("$[1].street")
                        .value("Oak Avenue"));

        verify(addressService)
                .findAuthenticatedUserAddresses();
    }

    @Test
    void updateAuthenticatedUserAddress_shouldReturn200_whenRequestIsValid()
            throws Exception {

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

        when(addressService.updateAuthenticatedUserAddress(
                eq(1L),
                any(AddressUpdateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_UPDATE_ADDRESS_REQUEST)
        )
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(1))

                .andExpect(jsonPath("$.street")
                        .value("Ocean Drive"))

                .andExpect(jsonPath("$.city")
                        .value("Miami"));

        verify(addressService).updateAuthenticatedUserAddress(eq(1L),
                any(AddressUpdateRequestDTO.class));
    }

    @Test
    void updateAuthenticatedUserAddress_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_UPDATE_ADDRESS_REQUEST))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(addressService);
    }

    @Test
    void deleteAuthenticatedUserAddress_shouldReturn204_whenAddressIsDeleted()
            throws Exception {

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(addressService).deleteAuthenticatedUserAddress(1L);
    }

    @Test
    void deleteAuthenticatedUserAddress_shouldReturn404_whenAddressDoesNotExist()
            throws Exception {

        doThrow(new ResourceNotFoundException(
                "Address not found for the authenticated user."))

                .when(addressService).deleteAuthenticatedUserAddress(99L);

        mockMvc.perform(delete(BASE_URL + "/99"))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("Address not found for the authenticated user."));

        verify(addressService).deleteAuthenticatedUserAddress(99L);
    }
}

