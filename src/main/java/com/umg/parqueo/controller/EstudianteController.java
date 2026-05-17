package com.umg.parqueo.controller;

import com.umg.parqueo.dto.response.SolvenciaResponse;
import com.umg.parqueo.entity.Estudiante;
import com.umg.parqueo.service.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Perfil y validación de solvencia")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @GetMapping("/perfil")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Perfil del estudiante autenticado")
    public ResponseEntity<Map<String, Object>> miPerfil(@AuthenticationPrincipal UserDetails user) {
        Estudiante est = estudianteService.obtenerPorCorreo(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "id", est.getId(),
                "carne", est.getCarne(),
                "nombreCompleto", est.getNombreCompleto(),
                "correo", est.getUsuario().getCorreo(),
                "carrera", est.getCarrera().getNombre(),
                "fechaIngreso", est.getFechaIngreso()
        ));
    }

    @GetMapping("/mi-solvencia")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @Operation(summary = "Estado consolidado de solvencia y autorización (RF10)")
    public ResponseEntity<SolvenciaResponse> miSolvencia(@AuthenticationPrincipal UserDetails user) {
        Estudiante est = estudianteService.obtenerPorCorreo(user.getUsername());
        return ResponseEntity.ok(estudianteService.evaluarElegibilidad(est.getId()));
    }

    @GetMapping("/{id}/solvencia")
    @PreAuthorize("hasAnyRole('ADMIN','SEGURIDAD')")
    @Operation(summary = "Solvencia de un estudiante específico (uso interno)")
    public ResponseEntity<SolvenciaResponse> solvenciaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.evaluarElegibilidad(id));
    }
}
