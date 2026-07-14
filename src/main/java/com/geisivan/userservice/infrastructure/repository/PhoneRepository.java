package com.geisivan.userservice.infrastructure.repository;

import com.geisivan.userservice.domain.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    Optional<Phone> findByIdAndUserId(Long phoneId, Long userId);
}
