package com.geisivan.userservice.application.mapper;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import static com.geisivan.userservice.application.mapper.MapperUtils.mapList;
import com.geisivan.userservice.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserMapper {

    private final PhoneMapper phoneMapper;
    private final AddressMapper addressMapper;

    public User toEntity(AdminUserRequestDTO dto) {

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .status(dto.status())
                .addresses(mapList(dto.addresses(), addressMapper::toEntity))
                .phones(mapList(dto.phones(), phoneMapper::toEntity))
                .build();

        user.getAddresses().forEach(address -> address.setUser(user));
        user.getPhones().forEach(phone -> phone.setUser(user));

        return user;
    }
}
