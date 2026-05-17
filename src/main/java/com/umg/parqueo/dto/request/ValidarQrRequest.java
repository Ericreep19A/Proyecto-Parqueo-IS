package com.umg.parqueo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidarQrRequest {

    @NotBlank(message = "El token QR es obligatorio")
    private String qrToken;

    @NotNull(message = "El id del vehículo es obligatorio")
    private Long vehiculoId;
}
