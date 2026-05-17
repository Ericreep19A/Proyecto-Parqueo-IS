package com.umg.parqueo.dto.response;

import com.umg.parqueo.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehiculoResponse {
    private Long id;
    private String placa;
    private TipoVehiculo tipo;
    private String marca;
    private String modelo;
    private String color;
    private Integer anio;
    private Boolean activo;
}
