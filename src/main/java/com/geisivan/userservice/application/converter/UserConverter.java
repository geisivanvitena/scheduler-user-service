package com.geisivan.userservice.application.converter;

import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import static com.geisivan.userservice.application.converter.ConverterUtils.mapList;
import com.geisivan.userservice.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final AddressConverter addressConverter;
    private final PhoneConverter phoneConverter;

    public User toEntity(UserRequestDTO dto){
       return User.builder()
               .name(dto.name())
               .email(dto.email())
               .password(dto.password())
               .address(mapList(dto.address(),
                       addressConverter::toEntity))
               .phones(mapList(dto.phones(),
                       phoneConverter::toEntity))
               .build();
    }

    public UserResponseDTO toResponse(User entity){
        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                mapList(entity.getAddress(),
                        addressConverter::toResponse),
                mapList(entity.getPhones(),
                        phoneConverter::toResponse)
        );
    }
}
