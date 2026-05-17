package com.umg.parqueo.controller;

import com.umg.parqueo.dto.request.ValidarQrRequest;
import com.umg.parqueo.dto.response.AccesoResponse;
import com.umg.parqueo.service.AccesoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accesos")
@RequiredArgsConstructor
@Tag(name = "Accesos", description = "Validación de QR y registro de ingresos (RF07)")
public class AccesoController {

    private final AccesoService accesoService;

    @PostMapping("/validar")
    @PreAuthorize("hasAnyRole('SEGURIDAD','ADMIN','ESTUDIANTE')")
    @Operation(summary = "Valida un QR + vehículo y autoriza o deniega el ingreso")
    public ResponseEntity<AccesoResponse> validarIngreso(@Valid @RequestBody ValidarQrRequest request) {
        return ResponseEntity.ok(accesoService.validarIngreso(request));
    }
}
