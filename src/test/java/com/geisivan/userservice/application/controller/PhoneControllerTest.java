package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.application.service.PhoneService;
import com.geisivan.userservice.domain.enums.PhoneType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PhoneController.class)
@AutoConfigureMockMvc(addFilters = false)
class PhoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private PhoneService phoneService;

    private static final String BASE_URL =
            "/api/v1/users/me/phones";

    private static final String VALID_PHONE_REQUEST = """
        {
            "areaCode": "71",
            "phoneNumber": "999999999",
            "phoneType": "MOBILE"
        }
        """;

    private static final String INVALID_PHONE_REQUEST = """
        {
            "areaCode": "",
            "phoneNumber": "",
            "phoneType": null
        }
        """;

    @Test
    void createAuthenticatedUserPhone_shouldReturn201_whenRequestIsValid()
            throws Exception {

        PhoneResponseDTO response =
                new PhoneResponseDTO(
                        1L,
                        "71",
                        "999999999",
                        PhoneType.MOBILE
                );

        when(phoneService.createAuthenticatedUserPhone(
                any(PhoneRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PHONE_REQUEST))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.areaCode").value("71"))
                .andExpect(jsonPath("$.phoneNumber").value("999999999"))
                .andExpect(jsonPath("$.phoneType").value("MOBILE"));

        verify(phoneService).createAuthenticatedUserPhone(
                any(PhoneRequestDTO.class));

    }

    @Test
    void createAuthenticatedUserPhone_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_PHONE_REQUEST))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(phoneService);
    }
}
