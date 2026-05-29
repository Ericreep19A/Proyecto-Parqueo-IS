package com.umg.parqueo.service;
 
import com.umg.parqueo.dto.request.ValidarQrRequest;
import com.umg.parqueo.dto.response.AccesoResponse;
import com.umg.parqueo.dto.response.SolvenciaResponse;
import com.umg.parqueo.entity.*;
import com.umg.parqueo.enums.EstadoMarbete;
import com.umg.parqueo.enums.ResultadoAcceso;
import com.umg.parqueo.enums.TipoVehiculo;
import com.umg.parqueo.exception.ReglaNegocioException;
import com.umg.parqueo.repository.MarbeteDigitalRepository;
import com.umg.parqueo.repository.RegistroAccesoRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
 
import java.time.LocalDate;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
 
/**
 * Pruebas unitarias para AccesoService.
 *
 * Funcionalidad: validar QR + vehículo + elegibilidad y registrar el ingreso.
 * RF cubiertos: RF06 (validación QR), RF07 (denegación), RF08 (sugerencia espacio).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Pruebas Unitarias - AccesoService (Validación de Ingreso)")
public class AccesoServiceTest {
 
    @Mock private QrService qrService;
    @Mock private EstudianteService estudianteService;
    @Mock private VehiculoService vehiculoService;
    @Mock private EspacioService espacioService;
    @Mock private MarbeteDigitalRepository marbeteRepository;
    @Mock private RegistroAccesoRepository registroRepository;
 
    @InjectMocks
    private AccesoService accesoService;
 
    private Estudiante estudiante;
    private Vehiculo vehiculo;
    private MarbeteDigital marbete;
    private EspacioEstacionamiento espacio;
    private Claims claims;
 
    @BeforeEach
    void setUp() {
        Usuario usuario = Usuario.builder().id(1L).correo("brandon.jom@miumg.edu.gt").build();
        estudiante = Estudiante.builder()
                .id(1L).usuario(usuario).carne("0910-22-3399")
                .nombres("Brandon Vicente").apellidos("Jom Velásquez").build();
 
        vehiculo = Vehiculo.builder()
                .id(1L).estudiante(estudiante).placa("P-123ABC")
                .tipo(TipoVehiculo.CARRO).marca("Honda").modelo("Civic")
                .activo(true).build();
 
        Semestre s = Semestre.builder().id(1).codigo("2026-1").build();
        marbete = MarbeteDigital.builder()
                .id(1L).estudiante(estudiante).semestre(s)
                .codigoUnico("MRB-2026-1-ABC12345")
                .fechaVigenciaInicio(LocalDate.now().minusDays(10))
                .fechaVigenciaFin(LocalDate.now().plusMonths(5))
                .estado(EstadoMarbete.ACTIVO).build();
 
        espacio = EspacioEstacionamiento.builder()
                .id(1).codigo("A-01").zona("Zona A").tipo(TipoVehiculo.CARRO).activo(true).build();
 
        // Mock de Claims usando Mockito.mock()
        claims = mock(Claims.class);
        when(claims.get("estudianteId")).thenReturn("1");
        when(claims.get("marbete", String.class)).thenReturn("MRB-2026-1-ABC12345");
    }
 
    @Test
    @DisplayName("CP-ACC-01: Acceso AUTORIZADO con todo en regla")
    void testValidarIngreso_Autorizado() {
        // ARRANGE
        ValidarQrRequest req = new ValidarQrRequest();
        req.setQrToken("token-valido");
        req.setVehiculoId(1L);
 
        when(qrService.validarToken("token-valido")).thenReturn(claims);
        when(estudianteService.obtenerPorId(1L)).thenReturn(estudiante);
        when(vehiculoService.obtener(1L)).thenReturn(vehiculo);
 
        SolvenciaResponse elegibilidad = SolvenciaResponse.builder()
                .autorizadoIngreso(true)
                .mensaje("Estudiante autorizado para ingresar al estacionamiento")
                .build();
        when(estudianteService.evaluarElegibilidad(1L)).thenReturn(elegibilidad);
        when(marbeteRepository.findByCodigoUnico("MRB-2026-1-ABC12345"))
                .thenReturn(Optional.of(marbete));
        when(espacioService.sugerirEspacio(TipoVehiculo.CARRO)).thenReturn(espacio);
 
        when(registroRepository.save(any(RegistroAcceso.class)))
                .thenAnswer(inv -> {
                    RegistroAcceso r = inv.getArgument(0);
                    r.setId(100L);
                    return r;
                });
 
        // ACT
        AccesoResponse response = accesoService.validarIngreso(req);
 
        // ASSERT
        assertEquals(ResultadoAcceso.AUTORIZADO, response.getResultado());
        assertEquals("Ingreso autorizado", response.getMensaje());
        assertEquals("0910-22-3399", response.getCarne());
        assertEquals("P-123ABC", response.getPlacaVehiculo());
        assertEquals("A-01", response.getEspacioSugerido());
        assertNull(response.getMotivoDenegacion());
    }
 
    @Test
    @DisplayName("CP-ACC-02: Acceso DENEGADO por QR inválido (RF07)")
    void testValidarIngreso_QrInvalido_Denegado() {
        // ARRANGE
        ValidarQrRequest req = new ValidarQrRequest();
        req.setQrToken("token-falso");
        req.setVehiculoId(1L);
 
        when(qrService.validarToken("token-falso"))
                .thenThrow(new ReglaNegocioException("Código QR inválido o expirado"));
 
        // ACT
        AccesoResponse response = accesoService.validarIngreso(req);
 
        // ASSERT
        assertEquals(ResultadoAcceso.DENEGADO, response.getResultado());
        assertEquals("Código QR inválido o expirado", response.getMensaje());
        assertEquals("Código QR inválido o expirado", response.getMotivoDenegacion());
    }
 
    @Test
    @DisplayName("CP-ACC-03: Acceso DENEGADO porque vehículo no pertenece al estudiante")
    void testValidarIngreso_VehiculoAjeno_Denegado() {
        // ARRANGE: vehículo de otro estudiante
        Estudiante otroEstudiante = Estudiante.builder().id(2L).carne("9999-99-9999").build();
        Vehiculo vehiculoAjeno = Vehiculo.builder()
                .id(2L).estudiante(otroEstudiante).placa("P-999XXX")
                .tipo(TipoVehiculo.CARRO).build();
 
        ValidarQrRequest req = new ValidarQrRequest();
        req.setQrToken("token-valido");
        req.setVehiculoId(2L);
 
        when(qrService.validarToken("token-valido")).thenReturn(claims);
        when(estudianteService.obtenerPorId(1L)).thenReturn(estudiante);
        when(vehiculoService.obtener(2L)).thenReturn(vehiculoAjeno);
        when(registroRepository.save(any(RegistroAcceso.class)))
                .thenAnswer(inv -> {
                    RegistroAcceso r = inv.getArgument(0);
                    r.setId(100L);
                    return r;
                });
 
        // ACT
        AccesoResponse response = accesoService.validarIngreso(req);
 
        // ASSERT
        assertEquals(ResultadoAcceso.DENEGADO, response.getResultado());
        assertEquals("El vehículo no pertenece al estudiante", response.getMensaje());
    }
 
    @Test
    @DisplayName("CP-ACC-04: Acceso DENEGADO porque estudiante no es solvente (RF07)")
    void testValidarIngreso_NoSolvente_Denegado() {
        // ARRANGE
        ValidarQrRequest req = new ValidarQrRequest();
        req.setQrToken("token-valido");
        req.setVehiculoId(1L);
 
        when(qrService.validarToken("token-valido")).thenReturn(claims);
        when(estudianteService.obtenerPorId(1L)).thenReturn(estudiante);
        when(vehiculoService.obtener(1L)).thenReturn(vehiculo);
 
        SolvenciaResponse noSolvente = SolvenciaResponse.builder()
                .autorizadoIngreso(false)
                .mensaje("Pago del estacionamiento no vigente")
                .build();
        when(estudianteService.evaluarElegibilidad(1L)).thenReturn(noSolvente);
        when(registroRepository.save(any(RegistroAcceso.class)))
                .thenAnswer(inv -> {
                    RegistroAcceso r = inv.getArgument(0);
                    r.setId(101L);
                    return r;
                });
 
        // ACT
        AccesoResponse response = accesoService.validarIngreso(req);
 
        // ASSERT
        assertEquals(ResultadoAcceso.DENEGADO, response.getResultado());
        assertEquals("Pago del estacionamiento no vigente", response.getMotivoDenegacion());
    }
 
    @Test
    @DisplayName("CP-ACC-05: Acceso DENEGADO porque marbete está vencido")
    void testValidarIngreso_MarbeteVencido_Denegado() {
        // ARRANGE
        MarbeteDigital marbeteVencido = MarbeteDigital.builder()
                .id(1L).estudiante(estudiante).codigoUnico("MRB-2026-1-ABC12345")
                .fechaVigenciaInicio(LocalDate.now().minusYears(1))
                .fechaVigenciaFin(LocalDate.now().minusMonths(6))
                .estado(EstadoMarbete.VENCIDO).build();
 
        ValidarQrRequest req = new ValidarQrRequest();
        req.setQrToken("token-valido");
        req.setVehiculoId(1L);
 
        when(qrService.validarToken("token-valido")).thenReturn(claims);
        when(estudianteService.obtenerPorId(1L)).thenReturn(estudiante);
        when(vehiculoService.obtener(1L)).thenReturn(vehiculo);
 
        SolvenciaResponse autorizado = SolvenciaResponse.builder()
                .autorizadoIngreso(true).mensaje("OK").build();
        when(estudianteService.evaluarElegibilidad(1L)).thenReturn(autorizado);
        when(marbeteRepository.findByCodigoUnico("MRB-2026-1-ABC12345"))
                .thenReturn(Optional.of(marbeteVencido));
        when(registroRepository.save(any(RegistroAcceso.class)))
                .thenAnswer(inv -> {
                    RegistroAcceso r = inv.getArgument(0);
                    r.setId(102L);
                    return r;
                });
 
        // ACT
        AccesoResponse response = accesoService.validarIngreso(req);
 
        // ASSERT
        assertEquals(ResultadoAcceso.DENEGADO, response.getResultado());
        assertEquals("Marbete digital no vigente", response.getMotivoDenegacion());
    }
}