package com.umg.parqueo.controller;

import com.umg.parqueo.dto.response.QrResponse;
import com.umg.parqueo.service.QrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
@Tag(name = "QR", description = "Generación del código QR personal (RF06)")
public class QrController {

    private final QrService qrService;

    @GetMapping("/mi-qr")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Genera el QR personal del estudiante autenticado (PNG en Base64)")
    public ResponseEntity<QrResponse> miQr(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(qrService.generarQrParaEstudiante(user.getUsername()));
    }
}
