package com.umg.parqueo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolvenciaResponse {
    private Long estudianteId;
    private String carne;
    private String nombreCompleto;
    private String semestre;
    private boolean inscritoEnSemestre;
    private boolean solventeAcademico;
    private boolean solventeFinanciero;
    private boolean pagoEstacionamientoVigente;
    private boolean marbeteVigente;
    private boolean autorizadoIngreso;
    private String mensaje;
}
