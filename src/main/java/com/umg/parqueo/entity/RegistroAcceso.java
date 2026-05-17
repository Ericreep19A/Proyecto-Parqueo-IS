package com.umg.parqueo.entity;

import com.umg.parqueo.enums.ResultadoAcceso;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_acceso")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegistroAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marbete_id")
    private MarbeteDigital marbete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_sugerido_id")
    private EspacioEstacionamiento espacioSugerido;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultadoAcceso resultado;

    @Column(name = "motivo_denegacion", length = 255)
    private String motivoDenegacion;

    @Column(name = "qr_token", length = 255)
    private String qrToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por")
    private Usuario validadoPor;

    @PrePersist
    void onCreate() {
        if (fechaHora == null) fechaHora = LocalDateTime.now();
    }
}
