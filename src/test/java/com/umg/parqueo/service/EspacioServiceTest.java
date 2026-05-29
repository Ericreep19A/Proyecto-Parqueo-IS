package com.umg.parqueo.service;

import com.umg.parqueo.entity.EspacioEstacionamiento;
import com.umg.parqueo.enums.TipoVehiculo;
import com.umg.parqueo.repository.EspacioEstacionamientoRepository;
import com.umg.parqueo.repository.InscripcionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para EspacioService.
 *
 * Funcionalidad: sugerir espacios y análisis de afluencia.
 * RF cubiertos: RF08 (sugerencia de espacio), RF09 (horas de mayor afluencia).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - EspacioService (RF08, RF09)")
public class EspacioServiceTest {

    @Mock
    private EspacioEstacionamientoRepository espacioRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private EspacioService espacioService;

    @Test
    @DisplayName("CP-ESP-01: Listar espacios disponibles por tipo de vehículo")
    void testListarDisponiblesPorTipo() {
        // ARRANGE
        EspacioEstacionamiento e1 = EspacioEstacionamiento.builder()
                .id(1).codigo("A-01").zona("Zona A").tipo(TipoVehiculo.CARRO).activo(true).build();
        EspacioEstacionamiento e2 = EspacioEstacionamiento.builder()
                .id(2).codigo("A-02").zona("Zona A").tipo(TipoVehiculo.CARRO).activo(true).build();

        when(espacioRepository.findByTipoAndActivoTrue(TipoVehiculo.CARRO))
                .thenReturn(Arrays.asList(e1, e2));

        // ACT
        List<EspacioEstacionamiento> result = espacioService.listarDisponiblesPorTipo(TipoVehiculo.CARRO);

        // ASSERT
        assertEquals(2, result.size());
        assertEquals("A-01", result.get(0).getCodigo());
        assertEquals("A-02", result.get(1).getCodigo());
    }

    @Test
    @DisplayName("CP-ESP-02: Sugerir espacio cuando hay disponibles (RF08)")
    void testSugerirEspacio_ConDisponibles() {
        // ARRANGE
        EspacioEstacionamiento e1 = EspacioEstacionamiento.builder()
                .id(1).codigo("A-01").zona("Zona A").tipo(TipoVehiculo.CARRO).activo(true).build();

        when(espacioRepository.findByTipoAndActivoTrue(TipoVehiculo.CARRO))
                .thenReturn(Collections.singletonList(e1));

        // ACT
        EspacioEstacionamiento result = espacioService.sugerirEspacio(TipoVehiculo.CARRO);

        // ASSERT
        assertNotNull(result);
        assertEquals("A-01", result.getCodigo());
        assertEquals(TipoVehiculo.CARRO, result.getTipo());
    }

    @Test
    @DisplayName("CP-ESP-03: Sugerir espacio sin disponibles - devuelve null")
    void testSugerirEspacio_SinDisponibles() {
        // ARRANGE
        when(espacioRepository.findByTipoAndActivoTrue(TipoVehiculo.MOTO))
                .thenReturn(Collections.emptyList());

        // ACT
        EspacioEstacionamiento result = espacioService.sugerirEspacio(TipoVehiculo.MOTO);

        // ASSERT
        assertNull(result, "Sin espacios disponibles debe retornar null");
    }

    @Test
    @DisplayName("CP-ESP-04: Identificar horas de mayor afluencia (RF09)")
    void testObtenerHorasAfluencia() {
        // ARRANGE
        Object[] fila1 = {8, 15L};  // 8AM, 15 estudiantes (más afluencia)
        Object[] fila2 = {14, 10L}; // 2PM, 10 estudiantes
        Object[] fila3 = {18, 5L};  // 6PM, 5 estudiantes

        when(inscripcionRepository.obtenerHorasAfluencia())
                .thenReturn(Arrays.asList(fila1, fila2, fila3));

        // ACT
        List<Map<String, Object>> result = espacioService.obtenerHorasAfluencia();

        // ASSERT
        assertEquals(3, result.size());
        // Primera fila: 8AM con 15 estudiantes (mayor afluencia)
        assertEquals(8, result.get(0).get("hora"));
        assertEquals(15L, result.get(0).get("cantidadEstudiantes"));
        // Segunda fila: 2PM con 10
        assertEquals(14, result.get(1).get("hora"));
        assertEquals(10L, result.get(1).get("cantidadEstudiantes"));
        // Tercera fila: 6PM con 5
        assertEquals(18, result.get(2).get("hora"));
    }
}
