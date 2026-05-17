package com.umg.parqueo.service;

import com.umg.parqueo.entity.EspacioEstacionamiento;
import com.umg.parqueo.enums.TipoVehiculo;
import com.umg.parqueo.repository.EspacioEstacionamientoRepository;
import com.umg.parqueo.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sugerencia de espacios (RF08, NO reserva) y análisis de horas pico (RF09).
 */
@Service
@RequiredArgsConstructor
public class EspacioService {

    private final EspacioEstacionamientoRepository espacioRepository;
    private final InscripcionRepository inscripcionRepository;

    @Transactional(readOnly = true)
    public List<EspacioEstacionamiento> listarDisponiblesPorTipo(TipoVehiculo tipo) {
        return espacioRepository.findByTipoAndActivoTrue(tipo);
    }

    /**
     * Sugiere un espacio para el tipo de vehículo indicado.
     * Estrategia simple del MVP: retorna el primero activo del tipo.
     * Nota: no reserva el espacio (alcance explícito del MVP).
     */
    @Transactional(readOnly = true)
    public EspacioEstacionamiento sugerirEspacio(TipoVehiculo tipo) {
        List<EspacioEstacionamiento> espacios = espacioRepository.findByTipoAndActivoTrue(tipo);
        if (espacios.isEmpty()) return null;
        return espacios.get(0);
    }

    /**
     * RF09: identifica horas de mayor afluencia según horarios de salida.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerHorasAfluencia() {
        return inscripcionRepository.obtenerHorasAfluencia().stream()
                .map(fila -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("hora", fila[0]);
                    m.put("cantidadEstudiantes", fila[1]);
                    return m;
                })
                .toList();
    }
}
