package com.umg.parqueo.controller;

import com.umg.parqueo.dto.response.MarbeteResponse;
import com.umg.parqueo.entity.Estudiante;
import com.umg.parqueo.service.EstudianteService;
import com.umg.parqueo.service.MarbeteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/marbetes")
@RequiredArgsConstructor
@Tag(name = "Marbete Digital", description = "Generación y consulta del marbete (RF04, RF05)")
public class MarbeteController {

    private final MarbeteService marbeteService;
    private final EstudianteService estudianteService;

    @GetMapping("/mi-marbete")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Marbete vigente del estudiante autenticado")
    public ResponseEntity<MarbeteResponse> miMarbete(@AuthenticationPrincipal UserDetails user) {
        Estudiante est = estudianteService.obtenerPorCorreo(user.getUsername());
        return ResponseEntity.ok(marbeteService.obtenerMarbeteVigente(est.getId()));
    }

    @PostMapping("/generar")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Genera un marbete digital nuevo (requiere pago vigente)")
    public ResponseEntity<MarbeteResponse> generarMarbete(@AuthenticationPrincipal UserDetails user) {
        Estudiante est = estudianteService.obtenerPorCorreo(user.getUsername());
        return ResponseEntity.ok(marbeteService.generarMarbete(est.getId()));
    }
}
