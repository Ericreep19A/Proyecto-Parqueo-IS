package com.umg.parqueo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QrResponse {
    /** Token codificado dentro del QR (firmado por el servidor). */
    private String qrToken;
    /** Imagen PNG del QR en Base64 — útil para mostrar en frontend. */
    private String qrImagenBase64;
    private LocalDateTime emitidoEn;
    private LocalDateTime expiraEn;
}
