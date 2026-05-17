package com.umg.parqueo.service;

import com.umg.parqueo.dto.response.MarbeteResponse;
import com.umg.parqueo.entity.*;
import com.umg.parqueo.enums.EstadoMarbete;
import com.umg.parqueo.enums.EstadoPago;
import com.umg.parqueo.exception.RecursoNoEncontradoException;
import com.umg.parqueo.exception.ReglaNegocioException;
import com.umg.parqueo.repository.MarbeteDigitalRepository;
import com.umg.parqueo.repository.PagoEstacionamientoRepository;
import com.umg.parqueo.repository.SemestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementa RF04 (generación de marbete) y RF05 (validación de vigencia).
 */
@Service
@RequiredArgsConstructor
public class MarbeteService {

    private final MarbeteDigitalRepository marbeteRepository;
    private final PagoEstacionamientoRepository pagoRepository;
    private final SemestreRepository semestreRepository;
    private final EstudianteService estudianteService;

    @Transactional(readOnly = true)
    public MarbeteResponse obtenerMarbeteVigente(Long estudianteId) {
        Semestre vigente = semestreRepository.findByVigenteTrue()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay un semestre vigente configurado"));

        MarbeteDigital m = marbeteRepository
                .findByEstudiante_IdAndSemestre_IdAndEstado(
                        estudianteId, vigente.getId(), EstadoMarbete.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El estudiante no posee un marbete digital activo en el semestre vigente"));

        return toResponse(m, vigente);
    }

    /**
     * Genera un marbete nuevo para el estudiante, basándose en un pago vigente.
     * No se ejecuta automáticamente: el estudiante (o el sistema tras detectar pago) lo solicita.
     */
    @Transactional
    public MarbeteResponse generarMarbete(Long estudianteId) {
        Estudiante est = estudianteService.obtenerPorId(estudianteId);
        Semestre vigente = semestreRepository.findByVigenteTrue()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay un semestre vigente configurado"));

        // Debe existir un pago VIGENTE
        PagoEstacionamiento pago = pagoRepository
                .findByEstudiante_IdAndSemestre_IdAndEstado(
                        est.getId(), vigente.getId(), EstadoPago.VIGENTE)
                .orElseThrow(() -> new ReglaNegocioException(
                        "No es posible generar marbete: el estudiante no tiene pago vigente"));

        // No duplicar
        marbeteRepository.findByEstudiante_IdAndSemestre_IdAndEstado(
                        est.getId(), vigente.getId(), EstadoMarbete.ACTIVO)
                .ifPresent(m -> { throw new ReglaNegocioException(
                        "El estudiante ya posee un marbete activo en este semestre"); });

        String codigo = "MRB-" + vigente.getCodigo() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        MarbeteDigital nuevo = MarbeteDigital.builder()
                .estudiante(est)
                .pago(pago)
                .semestre(vigente)
                .codigoUnico(codigo)
                .fechaVigenciaInicio(vigente.getFechaInicio())
                .fechaVigenciaFin(vigente.getFechaFin())
                .estado(EstadoMarbete.ACTIVO)
                .build();

        return toResponse(marbeteRepository.save(nuevo), vigente);
    }

    public MarbeteDigital buscarPorCodigo(String codigoUnico) {
        return marbeteRepository.findByCodigoUnico(codigoUnico)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Marbete no encontrado: " + codigoUnico));
    }

    private MarbeteResponse toResponse(MarbeteDigital m, Semestre s) {
        return MarbeteResponse.builder()
                .id(m.getId())
                .codigoUnico(m.getCodigoUnico())
                .semestre(s.getCodigo())
                .fechaEmision(m.getFechaEmision())
                .fechaVigenciaInicio(m.getFechaVigenciaInicio())
                .fechaVigenciaFin(m.getFechaVigenciaFin())
                .estado(m.getEstado())
                .vigente(m.isVigente())
                .build();
    }
}
