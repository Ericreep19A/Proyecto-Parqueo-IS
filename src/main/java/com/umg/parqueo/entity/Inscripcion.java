package com.umg.parqueo.entity;

import com.umg.parqueo.enums.EstadoInscripcion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "inscripciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(nullable = false, length = 5)
    private String seccion;

    @Column(name = "horario_salida")
    private LocalTime horarioSalida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estado;

    @Column(name = "fecha_inscripcion", nullable = false, updatable = false)
    private LocalDateTime fechaInscripcion;

    @PrePersist
    void onCreate() {
        if (fechaInscripcion == null) fechaInscripcion = LocalDateTime.now();
        if (estado == null) estado = EstadoInscripcion.ACTIVA;
    }
}
