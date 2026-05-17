package com.umg.parqueo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solvencias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Solvencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(name = "solvente_academico", nullable = false)
    private Boolean solventeAcademico;

    @Column(name = "solvente_financiero", nullable = false)
    private Boolean solventeFinanciero;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    void touch() {
        fechaActualizacion = LocalDateTime.now();
    }

    @Transient
    public boolean isSolventeTotal() {
        return Boolean.TRUE.equals(solventeAcademico) && Boolean.TRUE.equals(solventeFinanciero);
    }
}
