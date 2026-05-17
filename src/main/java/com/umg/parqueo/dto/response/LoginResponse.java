package com.umg.parqueo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String tipo;
    private Long usuarioId;
    private String correo;
    private String rol;
    private String nombreCompleto;
    private long expiraEnSegundos;
}
