package com.umg.parqueo.dto.request;

import com.umg.parqueo.enums.TipoVehiculo;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VehiculoRequest {

    @NotBlank
    @Size(max = 15)
    private String placa;

    @NotNull
    private TipoVehiculo tipo;

    @Size(max = 50)
    private String marca;

    @Size(max = 50)
    private String modelo;

    @Size(max = 30)
    private String color;

    @Min(1980) @Max(2030)
    private Integer anio;
}
