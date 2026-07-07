package com.geisivan.userservice.application.mapper;

import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import static com.geisivan.userservice.application.mapper.MapperUtils.mapList;
import static com.geisivan.userservice.application.mapper.MapperUtils.mapSet;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleMapper roleMapper;
    private final PhoneMapper phoneMapper;
    private final AddressMapper addressMapper;

    public User toEntity (UserRequestDTO dto) {
        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .addresses(
                        mapList(dto.addresses(),
                        addressMapper::toEntity)
                )
                .phones(
                        mapList(dto.phones(),
                        phoneMapper::toEntity)
                )
                .build();

        user.getAddresses()
                .forEach(address -> address.setUser(user));
        user.getPhones()
                .forEach(phone -> phone.setUser(user));
        return user;
    }

    public UserResponseDTO toDTO (User entity) {
        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getStatus(),
                mapSet(
                        entity.getRoles(),
                        roleMapper::toDTO
                ),
                mapList(
                        entity.getAddresses(),
                        addressMapper::toDTO
                ),
                mapList(
                        entity.getPhones(),
                        phoneMapper::toDTO
                ),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
