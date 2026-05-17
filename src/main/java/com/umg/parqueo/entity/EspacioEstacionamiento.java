package com.umg.parqueo.entity;

import com.umg.parqueo.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "espacios_estacionamiento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EspacioEstacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(nullable = false, length = 20)
    private String zona;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVehiculo tipo;

    @Column(nullable = false)
    private Boolean activo;
}
