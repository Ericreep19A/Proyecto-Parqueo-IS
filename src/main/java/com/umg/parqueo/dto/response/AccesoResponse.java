package com.umg.parqueo.dto.response;

import com.umg.parqueo.enums.ResultadoAcceso;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccesoResponse {
    private Long registroId;
    private ResultadoAcceso resultado;
    private String mensaje;
    private LocalDateTime fechaHora;
    private String nombreEstudiante;
    private String carne;
    private String placaVehiculo;
    private String espacioSugerido;
    private String motivoDenegacion;
}
