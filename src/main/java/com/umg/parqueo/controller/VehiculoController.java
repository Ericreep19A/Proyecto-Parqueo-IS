package com.umg.parqueo.controller;

import com.umg.parqueo.dto.request.VehiculoRequest;
import com.umg.parqueo.dto.response.VehiculoResponse;
import com.umg.parqueo.entity.Estudiante;
import com.umg.parqueo.service.EstudianteService;
import com.umg.parqueo.service.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "Vehículos asociados al estudiante")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final EstudianteService estudianteService;

    @GetMapping("/mis-vehiculos")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Lista los vehículos del estudiante autenticado")
    public ResponseEntity<List<VehiculoResponse>> misVehiculos(@AuthenticationPrincipal UserDetails user) {
        Estudiante est = estudianteService.obtenerPorCorreo(user.getUsername());
        return ResponseEntity.ok(vehiculoService.listarPorEstudiante(est.getId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Registra un vehículo nuevo")
    public ResponseEntity<VehiculoResponse> registrar(@AuthenticationPrincipal UserDetails user,
                                                      @Valid @RequestBody VehiculoRequest request) {
        Estudiante est = estudianteService.obtenerPorCorreo(user.getUsername());
        return ResponseEntity.ok(vehiculoService.registrar(est.getId(), request));
    }
}
