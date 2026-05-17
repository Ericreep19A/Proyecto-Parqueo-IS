package com.umg.parqueo.dto.response;

import com.umg.parqueo.enums.EstadoMarbete;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MarbeteResponse {
    private Long id;
    private String codigoUnico;
    private String semestre;
    private LocalDateTime fechaEmision;
    private LocalDate fechaVigenciaInicio;
    private LocalDate fechaVigenciaFin;
    private EstadoMarbete estado;
    private boolean vigente;
}
