package com.umg.parqueo.controller;

import com.umg.parqueo.entity.EspacioEstacionamiento;
import com.umg.parqueo.enums.TipoVehiculo;
import com.umg.parqueo.service.EspacioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/espacios")
@RequiredArgsConstructor
@Tag(name = "Espacios", description = "Sugerencia de espacios (RF08) y horas pico (RF09)")
public class EspacioController {

    private final EspacioService espacioService;

    @GetMapping("/disponibles")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lista de espacios disponibles del tipo indicado")
    public ResponseEntity<List<EspacioEstacionamiento>> disponibles(@RequestParam TipoVehiculo tipo) {
        return ResponseEntity.ok(espacioService.listarDisponiblesPorTipo(tipo));
    }

    @GetMapping("/sugerir")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Sugerencia de un espacio (no reserva)")
    public ResponseEntity<EspacioEstacionamiento> sugerir(@RequestParam TipoVehiculo tipo) {
        EspacioEstacionamiento e = espacioService.sugerirEspacio(tipo);
        return e == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(e);
    }

    @GetMapping("/horas-afluencia")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Análisis de horas de mayor afluencia (RF09)")
    public ResponseEntity<List<Map<String, Object>>> horasAfluencia() {
        return ResponseEntity.ok(espacioService.obtenerHorasAfluencia());
    }
}
