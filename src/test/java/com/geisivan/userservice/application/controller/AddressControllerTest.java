package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.AddressRequestDTO;
import com.geisivan.userservice.application.dto.response.AddressResponseDTO;
import com.geisivan.userservice.application.service.AddressService;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import com.geisivan.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void createAddress_shouldReturn201_whenRequestIsValid()
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
    void createAddress_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_ADDRESS_REQUEST))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(addressService);
    }
}

