package com.geisivan.userservice.infrastructure.security.auth;

import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;

public record MainUser(
        Long id,
        String email,
        String password,
        Set<RoleName> roles,
        UserStatus status

) implements UserDetails {

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    @NonNull
    public String getUsername() {
        return email;
    }

    @Override
    @NonNull
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BLOCKED;
    }
}
