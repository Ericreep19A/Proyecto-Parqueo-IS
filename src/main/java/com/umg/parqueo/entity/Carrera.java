package com.umg.parqueo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carreras")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String facultad;
}
