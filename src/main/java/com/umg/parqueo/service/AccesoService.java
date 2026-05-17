package com.umg.parqueo.service;

import com.umg.parqueo.dto.request.ValidarQrRequest;
import com.umg.parqueo.dto.response.AccesoResponse;
import com.umg.parqueo.dto.response.SolvenciaResponse;
import com.umg.parqueo.entity.*;
import com.umg.parqueo.enums.ResultadoAcceso;
import com.umg.parqueo.exception.RecursoNoEncontradoException;
import com.umg.parqueo.exception.ReglaNegocioException;
import com.umg.parqueo.repository.MarbeteDigitalRepository;
import com.umg.parqueo.repository.RegistroAccesoRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio núcleo: valida el QR, verifica elegibilidad y registra el ingreso.
 * Implementa el flujo completo del MVP: RF01-RF08.
 */
@Service
@RequiredArgsConstructor
public class AccesoService {

    private final QrService qrService;
    private final EstudianteService estudianteService;
    private final VehiculoService vehiculoService;
    private final EspacioService espacioService;
    private final MarbeteDigitalRepository marbeteRepository;
    private final RegistroAccesoRepository registroRepository;

    /**
     * Endpoint principal: el personal de seguridad envía el QR + vehiculo,
     * y el sistema responde AUTORIZADO o DENEGADO en menos de 3 segundos (RNF-01).
     */
    @Transactional
    public AccesoResponse validarIngreso(ValidarQrRequest req) {
        // 1. Validar firma y expiración del QR (RF06)
        Claims claims;
        try {
            claims = qrService.validarToken(req.getQrToken());
        } catch (ReglaNegocioException ex) {
            return registrarYResponder(null, null, null, null,
                    ResultadoAcceso.DENEGADO,
                    "Código QR inválido o expirado",
                    req.getQrToken());
        }

        Long estudianteId = Long.valueOf(claims.get("estudianteId").toString());
        Estudiante estudiante;
        try {
            estudiante = estudianteService.obtenerPorId(estudianteId);
        } catch (RecursoNoEncontradoException ex) {
            return registrarYResponder(null, null, null, null,
                    ResultadoAcceso.DENEGADO,
                    "Estudiante no encontrado en el sistema",
                    req.getQrToken());
        }

        // 2. Validar vehículo seleccionado (RF: estudiante con varios vehiculos)
        Vehiculo vehiculo = vehiculoService.obtener(req.getVehiculoId());
        if (!vehiculo.getEstudiante().getId().equals(estudiante.getId())) {
            return registrarYResponder(estudiante, vehiculo, null, null,
                    ResultadoAcceso.DENEGADO,
                    "El vehículo no pertenece al estudiante",
                    req.getQrToken());
        }

        // 3. Evaluar elegibilidad completa (RF02, RF03, RF05, RF07)
        SolvenciaResponse elegibilidad = estudianteService.evaluarElegibilidad(estudiante.getId());
        if (!elegibilidad.isAutorizadoIngreso()) {
            return registrarYResponder(estudiante, vehiculo, null, null,
                    ResultadoAcceso.DENEGADO,
                    elegibilidad.getMensaje(),
                    req.getQrToken());
        }

        // 4. Marbete vigente
        String codigoMarbete = claims.get("marbete", String.class);
        MarbeteDigital marbete = marbeteRepository.findByCodigoUnico(codigoMarbete)
                .orElse(null);
        if (marbete == null || !marbete.isVigente()) {
            return registrarYResponder(estudiante, vehiculo, null, null,
                    ResultadoAcceso.DENEGADO,
                    "Marbete digital no vigente",
                    req.getQrToken());
        }

        // 5. Sugerir espacio (RF08, sin reservar)
        EspacioEstacionamiento espacio = espacioService.sugerirEspacio(vehiculo.getTipo());

        // 6. Autorizado: registrar evento
        return registrarYResponder(estudiante, vehiculo, marbete, espacio,
                ResultadoAcceso.AUTORIZADO,
                "Ingreso autorizado",
                req.getQrToken());
    }

    private AccesoResponse registrarYResponder(Estudiante est, Vehiculo veh,
                                               MarbeteDigital marbete,
                                               EspacioEstacionamiento espacio,
                                               ResultadoAcceso resultado,
                                               String mensaje,
                                               String token) {
        RegistroAcceso registro = null;
        // Sólo registramos si tenemos al menos estudiante + vehiculo
        if (est != null && veh != null) {
            registro = RegistroAcceso.builder()
                    .estudiante(est)
                    .vehiculo(veh)
                    .marbete(marbete)
                    .espacioSugerido(espacio)
                    .resultado(resultado)
                    .motivoDenegacion(resultado == ResultadoAcceso.DENEGADO ? mensaje : null)
                    .qrToken(token)
                    .build();
            registro = registroRepository.save(registro);
        }

        return AccesoResponse.builder()
                .registroId(registro != null ? registro.getId() : null)
                .resultado(resultado)
                .mensaje(mensaje)
                .fechaHora(registro != null ? registro.getFechaHora() : null)
                .nombreEstudiante(est != null ? est.getNombreCompleto() : null)
                .carne(est != null ? est.getCarne() : null)
                .placaVehiculo(veh != null ? veh.getPlaca() : null)
                .espacioSugerido(espacio != null ? espacio.getCodigo() : null)
                .motivoDenegacion(resultado == ResultadoAcceso.DENEGADO ? mensaje : null)
                .build();
    }
}
