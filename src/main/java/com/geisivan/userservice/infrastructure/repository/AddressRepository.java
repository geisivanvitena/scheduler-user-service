package com.geisivan.userservice.infrastructure.repository;

import com.geisivan.userservice.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
