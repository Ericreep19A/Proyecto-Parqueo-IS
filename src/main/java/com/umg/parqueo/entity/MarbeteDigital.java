package com.umg.parqueo.entity;

import com.umg.parqueo.enums.EstadoMarbete;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "marbetes_digitales")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MarbeteDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id", nullable = false)
    private PagoEstacionamiento pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(name = "codigo_unico", nullable = false, unique = true, length = 50)
    private String codigoUnico;

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_vigencia_inicio", nullable = false)
    private LocalDate fechaVigenciaInicio;

    @Column(name = "fecha_vigencia_fin", nullable = false)
    private LocalDate fechaVigenciaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMarbete estado;

    @PrePersist
    void onCreate() {
        if (fechaEmision == null) fechaEmision = LocalDateTime.now();
        if (estado == null) estado = EstadoMarbete.ACTIVO;
    }

    /** Verifica si el marbete está vigente al día de hoy. */
    @Transient
    public boolean isVigente() {
        LocalDate hoy = LocalDate.now();
        return estado == EstadoMarbete.ACTIVO
                && !hoy.isBefore(fechaVigenciaInicio)
                && !hoy.isAfter(fechaVigenciaFin);
    }
}
