package com.geisivan.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street", length = 150, nullable = false)
    private String street;

    @Column(name = "number", nullable = false)
    private Long number;

    @Column(name = "complement", length = 50)
    private String complement;

    @Column(name = "neighborhood", length = 100, nullable = false)
    private String neighborhood;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "state", length = 2, nullable = false)
    private String state;

    @Column(name = "zip", length = 9, nullable = false)
    private String zip;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
}
