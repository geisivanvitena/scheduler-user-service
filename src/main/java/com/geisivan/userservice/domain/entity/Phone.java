package com.geisivan.userservice.domain.entity;

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

    @Column(name = "ddd", length = 3, nullable = false)
    private String ddd;

    @Column(name = "number", length = 10, nullable = false)
    private String number;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
}
