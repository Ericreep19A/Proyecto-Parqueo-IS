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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para MarbeteService.
 *
 * Funcionalidad: generar marbete y consultar marbete vigente.
 * RF cubiertos: RF04 (generar marbete), RF05 (validar vigencia).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - MarbeteService (Marbete Digital)")
public class MarbeteServiceTest {

    @Mock private MarbeteDigitalRepository marbeteRepository;
    @Mock private PagoEstacionamientoRepository pagoRepository;
    @Mock private SemestreRepository semestreRepository;
    @Mock private EstudianteService estudianteService;

    @InjectMocks
    private MarbeteService marbeteService;

    private Estudiante estudiante;
    private Semestre semestreVigente;
    private PagoEstacionamiento pagoVigente;
    private MarbeteDigital marbeteActivo;

    @BeforeEach
    void setUp() {
        Usuario usuario = Usuario.builder().id(1L).correo("brandon.jom@miumg.edu.gt").build();
        estudiante = Estudiante.builder()
                .id(1L).usuario(usuario).carne("0910-22-3399")
                .nombres("Brandon Vicente").apellidos("Jom Velásquez").build();

        semestreVigente = Semestre.builder()
                .id(1).codigo("2026-1").anio(2026).numero(1)
                .fechaInicio(LocalDate.of(2026, 1, 15))
                .fechaFin(LocalDate.of(2026, 6, 15))
                .vigente(true).build();

        pagoVigente = PagoEstacionamiento.builder()
                .id(1L).estudiante(estudiante).semestre(semestreVigente)
                .estado(EstadoPago.VIGENTE).build();

        marbeteActivo = MarbeteDigital.builder()
                .id(1L).estudiante(estudiante).pago(pagoVigente).semestre(semestreVigente)
                .codigoUnico("MRB-2026-1-ABC12345")
                .fechaVigenciaInicio(LocalDate.now().minusDays(10))
                .fechaVigenciaFin(LocalDate.now().plusMonths(5))
                .estado(EstadoMarbete.ACTIVO).build();
    }

    @Test
    @DisplayName("CP-MARB-01: Obtener marbete vigente del estudiante (RF05)")
    void testObtenerMarbeteVigente_Existente() {
        // ARRANGE
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(marbeteRepository.findByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoMarbete.ACTIVO))
                .thenReturn(Optional.of(marbeteActivo));

        // ACT
        MarbeteResponse response = marbeteService.obtenerMarbeteVigente(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals("MRB-2026-1-ABC12345", response.getCodigoUnico());
        assertEquals(EstadoMarbete.ACTIVO, response.getEstado());
        assertTrue(response.isVigente(), "El marbete debe estar vigente");
        assertEquals("2026-1", response.getSemestre());
    }

    @Test
    @DisplayName("CP-MARB-02: Estudiante sin marbete activo - excepción")
    void testObtenerMarbeteVigente_NoExiste() {
        // ARRANGE
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(marbeteRepository.findByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoMarbete.ACTIVO))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> marbeteService.obtenerMarbeteVigente(1L));

        assertTrue(ex.getMessage().contains("no posee un marbete digital activo"));
    }

    @Test
    @DisplayName("CP-MARB-03: Generar marbete con pago vigente (RF04)")
    void testGenerarMarbete_PagoVigente_Exitoso() {
        // ARRANGE
        when(estudianteService.obtenerPorId(1L)).thenReturn(estudiante);
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(pagoRepository.findByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoPago.VIGENTE))
                .thenReturn(Optional.of(pagoVigente));
        when(marbeteRepository.findByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoMarbete.ACTIVO))
                .thenReturn(Optional.empty()); // no tiene marbete previo

        // El save retorna el marbete con datos asignados
        when(marbeteRepository.save(any(MarbeteDigital.class)))
                .thenAnswer(inv -> {
                    MarbeteDigital m = inv.getArgument(0);
                    m.setId(99L);
                    return m;
                });

        // ACT
        MarbeteResponse response = marbeteService.generarMarbete(1L);

        // ASSERT
        assertNotNull(response);
        assertNotNull(response.getCodigoUnico());
        assertTrue(response.getCodigoUnico().startsWith("MRB-2026-1-"),
                "Código debe iniciar con MRB-2026-1-");
        assertEquals(EstadoMarbete.ACTIVO, response.getEstado());
        assertEquals(semestreVigente.getFechaInicio(), response.getFechaVigenciaInicio());
        assertEquals(semestreVigente.getFechaFin(), response.getFechaVigenciaFin());
    }

    @Test
    @DisplayName("CP-MARB-04: Generar marbete SIN pago vigente - excepción")
    void testGenerarMarbete_SinPagoVigente_Falla() {
        // ARRANGE
        when(estudianteService.obtenerPorId(1L)).thenReturn(estudiante);
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(pagoRepository.findByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoPago.VIGENTE))
                .thenReturn(Optional.empty()); // sin pago

        // ACT & ASSERT
        ReglaNegocioException ex = assertThrows(
                ReglaNegocioException.class,
                () -> marbeteService.generarMarbete(1L));

        assertTrue(ex.getMessage().contains("no tiene pago vigente"));
    }

    @Test
    @DisplayName("CP-MARB-05: Generar marbete duplicado - excepción")
    void testGenerarMarbete_YaTieneActivo_Falla() {
        // ARRANGE
        when(estudianteService.obtenerPorId(1L)).thenReturn(estudiante);
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(pagoRepository.findByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoPago.VIGENTE))
                .thenReturn(Optional.of(pagoVigente));
        when(marbeteRepository.findByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoMarbete.ACTIVO))
                .thenReturn(Optional.of(marbeteActivo)); // YA tiene marbete

        // ACT & ASSERT
        ReglaNegocioException ex = assertThrows(
                ReglaNegocioException.class,
                () -> marbeteService.generarMarbete(1L));

        assertTrue(ex.getMessage().contains("ya posee un marbete activo"));
    }

    @Test
    @DisplayName("CP-MARB-06: Buscar marbete por código existente")
    void testBuscarPorCodigo_Existente() {
        // ARRANGE
        when(marbeteRepository.findByCodigoUnico("MRB-2026-1-ABC12345"))
                .thenReturn(Optional.of(marbeteActivo));

        // ACT
        MarbeteDigital result = marbeteService.buscarPorCodigo("MRB-2026-1-ABC12345");

        // ASSERT
        assertNotNull(result);
        assertEquals("MRB-2026-1-ABC12345", result.getCodigoUnico());
        assertTrue(result.isVigente());
    }
}
