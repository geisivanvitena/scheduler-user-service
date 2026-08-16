package com.geisivan.userservice.application.controller;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.request.PhoneUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import com.geisivan.userservice.application.service.PhoneService;
import com.geisivan.userservice.controller.PhoneController;
import com.geisivan.userservice.domain.enums.PhoneType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    private static final String VALID_UPDATE_PHONE_REQUEST = """
    {
        "areaCode": "71",
        "phoneNumber": "999999999",
        "phoneType": "OTHER"
    }
    """;

    private static final String INVALID_UPDATE_PHONE_REQUEST = """
    {
        "areaCode": "7",
        "phoneNumber": "123",
        "phoneType": "OTHER"
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

                .andExpect(jsonPath("$.id")
                        .value(1))

                .andExpect(jsonPath("$.areaCode")
                        .value("71"))

                .andExpect(jsonPath("$.phoneNumber")
                        .value("999999999"))

                .andExpect(jsonPath("$.phoneType")
                        .value("MOBILE"));

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

    @Test
    void findAuthenticatedUserPhones_shouldReturn200_whenPhonesExist()
            throws Exception {

        PhoneResponseDTO firstPhone =
                new PhoneResponseDTO(
                        1L,
                        "71",
                        "999887766",
                        PhoneType.MOBILE
                );

        PhoneResponseDTO secondPhone =
                new PhoneResponseDTO(
                        2L,
                        "41",
                        "992378825",
                        PhoneType.WORK
                );

        when(phoneService.findAuthenticatedUserPhones())
                .thenReturn(List.of(firstPhone, secondPhone));

        mockMvc.perform(get(BASE_URL))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()")
                        .value(2))

                .andExpect(jsonPath("$[0].id")
                        .value(1))

                .andExpect(jsonPath("$[0].areaCode")
                        .value("71"))

                .andExpect(jsonPath("$[0].phoneNumber")
                        .value("999887766"))

                .andExpect(jsonPath("$[1].id")
                        .value(2))

                .andExpect(jsonPath("$[1].areaCode")
                        .value("41"))

                .andExpect(jsonPath("$[1].phoneNumber")
                        .value("992378825"));

        verify(phoneService).findAuthenticatedUserPhones();
    }

    @Test
    void updateAuthenticatedUserPhone_shouldReturn200_whenRequestIsValid()
            throws Exception {

        PhoneResponseDTO response =
                new PhoneResponseDTO(
                        1L,
                        "71",
                        "999999999",
                        PhoneType.OTHER
                );

        when(phoneService.updateAuthenticatedUserPhone( eq(1L),
                any(PhoneUpdateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_UPDATE_PHONE_REQUEST))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(1))

                .andExpect(jsonPath("$.areaCode")
                        .value("71"))

                .andExpect(jsonPath("$.phoneNumber")
                        .value("999999999"))

                .andExpect(jsonPath("$.phoneType")
                        .value("OTHER"));

        verify(phoneService).updateAuthenticatedUserPhone( eq(1L),
                any(PhoneUpdateRequestDTO.class));
    }

    @Test
    void updateAuthenticatedUserPhone_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        mockMvc.perform( put(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(INVALID_UPDATE_PHONE_REQUEST) )

                .andExpect(status().isBadRequest());

        verifyNoInteractions(phoneService);
    }

    @Test
    void deleteAuthenticatedUserPhone_shouldReturn204_whenPhoneIsDeleted()
            throws Exception {

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(phoneService).deleteAuthenticatedUserPhone(1L);
    }

    @Test
    void deleteAuthenticatedUserPhone_shouldReturn404_whenPhoneDoesNotExist()
            throws Exception {

        doThrow(new ResourceNotFoundException(
                "Phone not found for the authenticated user."))

                .when(phoneService).deleteAuthenticatedUserPhone(99L);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.message")
                        .value("Phone not found for the authenticated user."));

        verify(phoneService).deleteAuthenticatedUserPhone(99L);
    }
}
