package com.umg.parqueo.service;

import com.umg.parqueo.dto.response.MarbeteResponse;
import com.umg.parqueo.dto.response.QrResponse;
import com.umg.parqueo.entity.*;
import com.umg.parqueo.enums.EstadoMarbete;
import com.umg.parqueo.exception.ReglaNegocioException;
import com.umg.parqueo.util.QRCodeGenerator;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para QrService.
 *
 * Funcionalidad: generar QR firmado con JWT y validar tokens QR.
 * RF cubiertos: RF06 (generación y validación de QR).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - QrService (Generación y Validación de QR)")
public class QrServiceTest {

    @Mock private QRCodeGenerator qrGenerator;
    @Mock private EstudianteService estudianteService;
    @Mock private MarbeteService marbeteService;

    @InjectMocks
    private QrService qrService;

    private static final String SECRET = "ClaveSuperSecretaUMG2026ParqueoControlAccesoVehicular!!";
    private static final long EXPIRACION_SEG = 300L; // 5 minutos

    private Estudiante estudiante;
    private MarbeteDigital marbeteVigente;
    private MarbeteResponse marbeteResponse;

    @BeforeEach
    void setUp() {
        // Inyectar @Value
        ReflectionTestUtils.setField(qrService, "secret", SECRET);
        ReflectionTestUtils.setField(qrService, "expiracionSegundos", EXPIRACION_SEG);

        Usuario usuario = Usuario.builder().id(1L).correo("brandon.jom@miumg.edu.gt").build();
        estudiante = Estudiante.builder()
                .id(1L).usuario(usuario).carne("0910-22-3399")
                .nombres("Brandon Vicente").apellidos("Jom Velásquez").build();

        Semestre semestre = Semestre.builder().id(1).codigo("2026-1").build();

        marbeteVigente = MarbeteDigital.builder()
                .id(1L).estudiante(estudiante).semestre(semestre)
                .codigoUnico("MRB-2026-1-ABC12345")
                .fechaVigenciaInicio(LocalDate.now().minusDays(10))
                .fechaVigenciaFin(LocalDate.now().plusMonths(5))
                .estado(EstadoMarbete.ACTIVO).build();

        marbeteResponse = MarbeteResponse.builder()
                .id(1L).codigoUnico("MRB-2026-1-ABC12345")
                .estado(EstadoMarbete.ACTIVO).vigente(true).build();
    }

    @Test
    @DisplayName("CP-QR-01: Generar QR para estudiante con marbete vigente")
    void testGenerarQr_MarbeteVigente_Exitoso() {
        // ARRANGE
        when(estudianteService.obtenerPorCorreo("brandon.jom@miumg.edu.gt"))
                .thenReturn(estudiante);
        when(marbeteService.obtenerMarbeteVigente(1L)).thenReturn(marbeteResponse);
        when(marbeteService.buscarPorCodigo("MRB-2026-1-ABC12345")).thenReturn(marbeteVigente);
        when(qrGenerator.generarQrBase64(anyString())).thenReturn("iVBORw0KGgoAAAANSUhEUgAA...");

        // ACT
        QrResponse response = qrService.generarQrParaEstudiante("brandon.jom@miumg.edu.gt");

        // ASSERT
        assertNotNull(response);
        assertNotNull(response.getQrToken(), "El token JWT del QR debe generarse");
        assertNotNull(response.getQrImagenBase64(), "La imagen Base64 debe generarse");
        assertNotNull(response.getEmitidoEn());
        assertNotNull(response.getExpiraEn());
        // El expiraEn debe estar ~5 minutos después de emitidoEn
        assertTrue(response.getExpiraEn().isAfter(response.getEmitidoEn()));
    }

    @Test
    @DisplayName("CP-QR-02: Validar token QR recién generado (válido)")
    void testValidarToken_TokenValido() {
        // ARRANGE: generamos un token primero
        when(estudianteService.obtenerPorCorreo("brandon.jom@miumg.edu.gt"))
                .thenReturn(estudiante);
        when(marbeteService.obtenerMarbeteVigente(1L)).thenReturn(marbeteResponse);
        when(marbeteService.buscarPorCodigo("MRB-2026-1-ABC12345")).thenReturn(marbeteVigente);
        when(qrGenerator.generarQrBase64(anyString())).thenReturn("base64...");

        QrResponse qr = qrService.generarQrParaEstudiante("brandon.jom@miumg.edu.gt");

        // ACT
        Claims claims = qrService.validarToken(qr.getQrToken());

        // ASSERT
        assertNotNull(claims);
        assertEquals("0910-22-3399", claims.getSubject(), "El subject debe ser el carné");
        assertEquals("MRB-2026-1-ABC12345", claims.get("marbete", String.class));
        assertEquals("Brandon Vicente Jom Velásquez", claims.get("nombre", String.class));
        assertEquals("umg-parqueo-qr", claims.getIssuer());
    }

    @Test
    @DisplayName("CP-QR-03: Validar token QR malformado - excepción")
    void testValidarToken_TokenMalformado_LanzaExcepcion() {
        // ARRANGE
        String tokenInvalido = "esto-no-es-un-jwt-valido";

        // ACT & ASSERT
        ReglaNegocioException ex = assertThrows(
                ReglaNegocioException.class,
                () -> qrService.validarToken(tokenInvalido));

        assertEquals("Código QR inválido o expirado", ex.getMessage());
    }

    @Test
    @DisplayName("CP-QR-04: Generar QR cuando NO hay marbete vigente - excepción")
    void testGenerarQr_SinMarbete_Falla() {
        // ARRANGE
        when(estudianteService.obtenerPorCorreo("brandon.jom@miumg.edu.gt"))
                .thenReturn(estudiante);
        when(marbeteService.obtenerMarbeteVigente(1L))
                .thenThrow(new com.umg.parqueo.exception.RecursoNoEncontradoException(
                        "No posee marbete activo"));

        // ACT & ASSERT
        ReglaNegocioException ex = assertThrows(
                ReglaNegocioException.class,
                () -> qrService.generarQrParaEstudiante("brandon.jom@miumg.edu.gt"));

        assertTrue(ex.getMessage().contains("No se puede generar QR"));
    }

    @Test
    @DisplayName("CP-QR-05: Validar token vacío - excepción")
    void testValidarToken_TokenVacio_LanzaExcepcion() {
        // ACT & ASSERT
        assertThrows(ReglaNegocioException.class,
                () -> qrService.validarToken(""));
    }
}
