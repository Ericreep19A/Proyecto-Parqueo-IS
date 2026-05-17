package com.umg.parqueo.service;

import com.umg.parqueo.dto.response.SolvenciaResponse;
import com.umg.parqueo.entity.*;
import com.umg.parqueo.enums.EstadoInscripcion;
import com.umg.parqueo.enums.EstadoMarbete;
import com.umg.parqueo.enums.EstadoPago;
import com.umg.parqueo.exception.RecursoNoEncontradoException;
import com.umg.parqueo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio del estudiante: validación consolidada de solvencia y elegibilidad
 * para ingresar al estacionamiento. Implementa RF01, RF02, RF03, RF05, RF07, RF10.
 */
@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final SemestreRepository semestreRepository;
    private final SolvenciaRepository solvenciaRepository;
    private final PagoEstacionamientoRepository pagoRepository;
    private final MarbeteDigitalRepository marbeteRepository;
    private final InscripcionRepository inscripcionRepository;

    public Estudiante obtenerPorCorreo(String correo) {
        return estudianteRepository.findByUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe estudiante asociado al correo " + correo));
    }

    public Estudiante obtenerPorId(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante con id " + id + " no encontrado"));
    }

    @Transactional(readOnly = true)
    public SolvenciaResponse evaluarElegibilidad(Long estudianteId) {
        Estudiante est = obtenerPorId(estudianteId);
        Semestre vigente = semestreRepository.findByVigenteTrue()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay un semestre vigente configurado"));

        // RF01: inscripcion en al menos un curso del semestre vigente
        long inscripcionesActivas = inscripcionRepository
                .countByEstudiante_IdAndSemestre_IdAndEstado(
                        est.getId(), vigente.getId(), EstadoInscripcion.ACTIVA);
        boolean inscrito = inscripcionesActivas > 0;

        // RF02: solvencia academica y financiera
        Solvencia sol = solvenciaRepository
                .findByEstudiante_IdAndSemestre_Id(est.getId(), vigente.getId())
                .orElse(null);
        boolean solvAcademico  = sol != null && Boolean.TRUE.equals(sol.getSolventeAcademico());
        boolean solvFinanciero = sol != null && Boolean.TRUE.equals(sol.getSolventeFinanciero());

        // RF03: pago de estacionamiento vigente
        boolean pagoVigente = pagoRepository
                .findByEstudiante_IdAndSemestre_IdAndEstado(
                        est.getId(), vigente.getId(), EstadoPago.VIGENTE)
                .isPresent();

        // RF05: marbete digital vigente
        boolean marbeteVigente = marbeteRepository
                .findByEstudiante_IdAndSemestre_IdAndEstado(
                        est.getId(), vigente.getId(), EstadoMarbete.ACTIVO)
                .map(MarbeteDigital::isVigente)
                .orElse(false);

        // RF07: autorización combinada
        boolean autorizado = inscrito && solvAcademico && solvFinanciero
                && pagoVigente && marbeteVigente;

        String mensaje = construirMensaje(inscrito, solvAcademico, solvFinanciero,
                                          pagoVigente, marbeteVigente);

        return SolvenciaResponse.builder()
                .estudianteId(est.getId())
                .carne(est.getCarne())
                .nombreCompleto(est.getNombreCompleto())
                .semestre(vigente.getCodigo())
                .inscritoEnSemestre(inscrito)
                .solventeAcademico(solvAcademico)
                .solventeFinanciero(solvFinanciero)
                .pagoEstacionamientoVigente(pagoVigente)
                .marbeteVigente(marbeteVigente)
                .autorizadoIngreso(autorizado)
                .mensaje(mensaje)
                .build();
    }

    private String construirMensaje(boolean inscrito, boolean acad, boolean fin,
                                    boolean pago, boolean marbete) {
        if (!inscrito)  return "No se encuentra inscrito en el semestre vigente";
        if (!acad)      return "Solvencia académica pendiente";
        if (!fin)       return "Solvencia financiera pendiente";
        if (!pago)      return "Pago del estacionamiento no vigente";
        if (!marbete)   return "Marbete digital no vigente o no emitido";
        return "Estudiante autorizado para ingresar al estacionamiento";
    }
}
