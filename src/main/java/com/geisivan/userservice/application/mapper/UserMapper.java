package com.geisivan.userservice.application.mapper;

import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import static com.geisivan.userservice.application.mapper.MapperUtils.mapList;
import static com.geisivan.userservice.application.mapper.MapperUtils.mapSet;
import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
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
                .addresses(mapList(dto.addresses(), addressMapper::toEntity))
                .phones(mapList(dto.phones(), phoneMapper::toEntity))
                .build();

        user.getAddresses().forEach(address -> address.setUser(user));
        user.getPhones().forEach(phone -> phone.setUser(user));

        return user;
    }

    public UserResponseDTO toDTO (User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus(),
                mapSet(user.getRoles(), roleMapper::toDTO),
                mapList(user.getAddresses(), addressMapper::toDTO),
                mapList(user.getPhones(), phoneMapper::toDTO),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public void update(UserUpdateRequestDTO dto, User user) {

        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            user.setEmail(dto.email());
        }
    }
}
