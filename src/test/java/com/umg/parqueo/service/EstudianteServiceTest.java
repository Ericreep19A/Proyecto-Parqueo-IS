package com.umg.parqueo.service;

import com.umg.parqueo.dto.response.SolvenciaResponse;
import com.umg.parqueo.entity.*;
import com.umg.parqueo.enums.EstadoInscripcion;
import com.umg.parqueo.enums.EstadoMarbete;
import com.umg.parqueo.enums.EstadoPago;
import com.umg.parqueo.exception.RecursoNoEncontradoException;
import com.umg.parqueo.repository.*;
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
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para EstudianteService.
 *
 * Funcionalidad principal: evaluarElegibilidad() - validación consolidada de
 * inscripción, solvencia académica/financiera, pago de estacionamiento y marbete.
 *
 * RF cubiertos: RF01 (inscripción), RF02 (solvencia), RF03 (pago),
 *               RF05 (marbete vigente), RF07 (denegación), RF10 (mostrar estado).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - EstudianteService (Solvencia y Elegibilidad)")
public class EstudianteServiceTest {

    @Mock private EstudianteRepository estudianteRepository;
    @Mock private SemestreRepository semestreRepository;
    @Mock private SolvenciaRepository solvenciaRepository;
    @Mock private PagoEstacionamientoRepository pagoRepository;
    @Mock private MarbeteDigitalRepository marbeteRepository;
    @Mock private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private EstudianteService estudianteService;

    private Estudiante estudiante;
    private Semestre semestreVigente;
    private Solvencia solvenciaCompleta;
    private PagoEstacionamiento pagoVigente;
    private MarbeteDigital marbeteActivo;

    @BeforeEach
    void setUp() {
        // Usuario y estudiante (Brandon)
        Usuario usuario = Usuario.builder()
                .id(1L).correo("brandon.jom@miumg.edu.gt").build();

        estudiante = Estudiante.builder()
                .id(1L).usuario(usuario).carne("0910-22-3399")
                .nombres("Brandon Vicente").apellidos("Jom Velásquez")
                .fechaIngreso(LocalDate.of(2022, 1, 1)).activo(true).build();

        // Semestre vigente
        semestreVigente = Semestre.builder()
                .id(1).codigo("2026-1").anio(2026).numero(1)
                .fechaInicio(LocalDate.of(2026, 1, 15))
                .fechaFin(LocalDate.of(2026, 6, 15))
                .vigente(true).build();

        // Solvencia completa (académica y financiera = true)
        solvenciaCompleta = Solvencia.builder()
                .id(1L).estudiante(estudiante).semestre(semestreVigente)
                .solventeAcademico(true).solventeFinanciero(true).build();

        // Pago vigente
        pagoVigente = PagoEstacionamiento.builder()
                .id(1L).estudiante(estudiante).semestre(semestreVigente)
                .estado(EstadoPago.VIGENTE).build();

        // Marbete activo y vigente
        marbeteActivo = MarbeteDigital.builder()
                .id(1L).estudiante(estudiante).semestre(semestreVigente)
                .codigoUnico("MRB-2026-1-ABC12345")
                .fechaVigenciaInicio(LocalDate.now().minusDays(10))
                .fechaVigenciaFin(LocalDate.now().plusMonths(5))
                .estado(EstadoMarbete.ACTIVO).build();
    }

    @Test
    @DisplayName("CP-EST-01: Obtener estudiante por correo existente")
    void testObtenerPorCorreo_Existente() {
        // ARRANGE
        when(estudianteRepository.findByUsuario_Correo("brandon.jom@miumg.edu.gt"))
                .thenReturn(Optional.of(estudiante));

        // ACT
        Estudiante result = estudianteService.obtenerPorCorreo("brandon.jom@miumg.edu.gt");

        // ASSERT
        assertNotNull(result);
        assertEquals("0910-22-3399", result.getCarne());
        assertEquals("Brandon Vicente Jom Velásquez", result.getNombreCompleto());
    }

    @Test
    @DisplayName("CP-EST-02: Obtener estudiante por correo inexistente lanza excepción")
    void testObtenerPorCorreo_NoExistente() {
        // ARRANGE
        when(estudianteRepository.findByUsuario_Correo("noexiste@miumg.edu.gt"))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> estudianteService.obtenerPorCorreo("noexiste@miumg.edu.gt"));

        assertTrue(ex.getMessage().contains("No existe estudiante"));
    }

    @Test
    @DisplayName("CP-EST-03: Estudiante completamente elegible - AUTORIZADO")
    void testEvaluarElegibilidad_TodoOk_Autorizado() {
        // ARRANGE: estudiante con TODO en orden
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(inscripcionRepository.countByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoInscripcion.ACTIVA)).thenReturn(3L);
        when(solvenciaRepository.findByEstudiante_IdAndSemestre_Id(1L, 1))
                .thenReturn(Optional.of(solvenciaCompleta));
        when(pagoRepository.findByEstudiante_IdAndSemestre_IdAndEstado(1L, 1, EstadoPago.VIGENTE))
                .thenReturn(Optional.of(pagoVigente));
        when(marbeteRepository.findByEstudiante_IdAndSemestre_IdAndEstado(1L, 1, EstadoMarbete.ACTIVO))
                .thenReturn(Optional.of(marbeteActivo));

        // ACT
        SolvenciaResponse response = estudianteService.evaluarElegibilidad(1L);

        // ASSERT
        assertTrue(response.isAutorizadoIngreso(), "Debe estar AUTORIZADO");
        assertTrue(response.isInscritoEnSemestre());
        assertTrue(response.isSolventeAcademico());
        assertTrue(response.isSolventeFinanciero());
        assertTrue(response.isPagoEstacionamientoVigente());
        assertTrue(response.isMarbeteVigente());
        assertEquals("Estudiante autorizado para ingresar al estacionamiento", response.getMensaje());
        assertEquals("0910-22-3399", response.getCarne());
    }

    @Test
    @DisplayName("CP-EST-04: Estudiante sin pago de estacionamiento - DENEGADO (RF07)")
    void testEvaluarElegibilidad_SinPago_Denegado() {
        // ARRANGE: todo OK menos el pago
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(inscripcionRepository.countByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoInscripcion.ACTIVA)).thenReturn(3L);
        when(solvenciaRepository.findByEstudiante_IdAndSemestre_Id(1L, 1))
                .thenReturn(Optional.of(solvenciaCompleta));
        when(pagoRepository.findByEstudiante_IdAndSemestre_IdAndEstado(1L, 1, EstadoPago.VIGENTE))
                .thenReturn(Optional.empty()); // SIN PAGO

        // ACT
        SolvenciaResponse response = estudianteService.evaluarElegibilidad(1L);

        // ASSERT
        assertFalse(response.isAutorizadoIngreso(), "NO debe estar autorizado sin pago");
        assertFalse(response.isPagoEstacionamientoVigente());
        assertEquals("Pago del estacionamiento no vigente", response.getMensaje());
    }

    @Test
    @DisplayName("CP-EST-05: Estudiante sin solvencia académica - DENEGADO")
    void testEvaluarElegibilidad_SinSolvenciaAcademica_Denegado() {
        // ARRANGE: solvencia académica = false
        Solvencia sinAcademica = Solvencia.builder()
                .estudiante(estudiante).semestre(semestreVigente)
                .solventeAcademico(false).solventeFinanciero(true).build();

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(inscripcionRepository.countByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoInscripcion.ACTIVA)).thenReturn(3L);
        when(solvenciaRepository.findByEstudiante_IdAndSemestre_Id(1L, 1))
                .thenReturn(Optional.of(sinAcademica));

        // ACT
        SolvenciaResponse response = estudianteService.evaluarElegibilidad(1L);

        // ASSERT
        assertFalse(response.isAutorizadoIngreso());
        assertFalse(response.isSolventeAcademico());
        assertEquals("Solvencia académica pendiente", response.getMensaje());
    }

    @Test
    @DisplayName("CP-EST-06: Estudiante no inscrito en semestre vigente - DENEGADO")
    void testEvaluarElegibilidad_NoInscrito_Denegado() {
        // ARRANGE
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(semestreRepository.findByVigenteTrue()).thenReturn(Optional.of(semestreVigente));
        when(inscripcionRepository.countByEstudiante_IdAndSemestre_IdAndEstado(
                1L, 1, EstadoInscripcion.ACTIVA)).thenReturn(0L); // NO inscrito

        // ACT
        SolvenciaResponse response = estudianteService.evaluarElegibilidad(1L);

        // ASSERT
        assertFalse(response.isAutorizadoIngreso());
        assertFalse(response.isInscritoEnSemestre());
        assertEquals("No se encuentra inscrito en el semestre vigente", response.getMensaje());
    }
}
