package com.umg.parqueo.entity;

import com.umg.parqueo.enums.EstadoPago;
import com.umg.parqueo.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos_estacionamiento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagoEstacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "no_recibo", nullable = false, unique = true, length = 50)
    private String noRecibo;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @PrePersist
    void onCreate() {
        if (fechaPago == null) fechaPago = LocalDateTime.now();
        if (estado == null) estado = EstadoPago.VIGENTE;
    }
}
