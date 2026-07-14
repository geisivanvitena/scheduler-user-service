package com.geisivan.userservice.infrastructure.repository;

import com.geisivan.userservice.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndUserId(Long addressId, Long userId);
}
