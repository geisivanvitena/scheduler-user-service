package com.geisivan.userservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geisivan.userservice.domain.enums.PhoneType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phones")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "area_code", length = 2, nullable = false)
    private String areaCode;

    @Column(name = "phone_number", length = 9, nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_type", nullable = false)
    private PhoneType phoneType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
