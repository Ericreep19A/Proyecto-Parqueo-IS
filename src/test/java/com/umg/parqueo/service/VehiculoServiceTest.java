package com.umg.parqueo.service;

import com.umg.parqueo.dto.request.VehiculoRequest;
import com.umg.parqueo.dto.response.VehiculoResponse;
import com.umg.parqueo.entity.Estudiante;
import com.umg.parqueo.entity.Usuario;
import com.umg.parqueo.entity.Vehiculo;
import com.umg.parqueo.enums.TipoVehiculo;
import com.umg.parqueo.exception.RecursoNoEncontradoException;
import com.umg.parqueo.exception.ReglaNegocioException;
import com.umg.parqueo.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para VehiculoService.
 *
 * Funcionalidad: Registrar y listar vehículos del estudiante.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - VehiculoService (Gestión de Vehículos)")
public class VehiculoServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private EstudianteService estudianteService;

    @InjectMocks
    private VehiculoService vehiculoService;

    private Estudiante estudiante;

    @BeforeEach
    void setUp() {
        Usuario usuario = Usuario.builder().id(1L).correo("brandon.jom@miumg.edu.gt").build();
        estudiante = Estudiante.builder()
                .id(1L).usuario(usuario).carne("0910-22-3399")
                .nombres("Brandon Vicente").apellidos("Jom Velásquez").build();
    }

    @Test
    @DisplayName("CP-VEH-01: Listar vehículos activos del estudiante")
    void testListarPorEstudiante() {
        // ARRANGE
        Vehiculo v1 = Vehiculo.builder()
                .id(1L).estudiante(estudiante).placa("P-123ABC")
                .tipo(TipoVehiculo.CARRO).marca("Honda").modelo("Civic")
                .color("Negro").anio(2020).activo(true).build();

        when(vehiculoRepository.findByEstudiante_IdAndActivoTrue(1L))
                .thenReturn(Collections.singletonList(v1));

        // ACT
        List<VehiculoResponse> result = vehiculoService.listarPorEstudiante(1L);

        // ASSERT
        assertEquals(1, result.size());
        assertEquals("P-123ABC", result.get(0).getPlaca());
        assertEquals(TipoVehiculo.CARRO, result.get(0).getTipo());
        assertEquals("Honda", result.get(0).getMarca());
    }

    @Test
    @DisplayName("CP-VEH-02: Registrar vehículo nuevo correctamente")
    void testRegistrarVehiculo_PlacaNueva() {
        // ARRANGE
        VehiculoRequest req = new VehiculoRequest();
        req.setPlaca("p-456xyz");  // en minúsculas
        req.setTipo(TipoVehiculo.MOTO);
        req.setMarca("Yamaha");
        req.setModelo("FZ");
        req.setColor("Rojo");
        req.setAnio(2024);

        when(vehiculoRepository.existsByPlaca("p-456xyz")).thenReturn(false);
        when(estudianteService.obtenerPorId(1L)).thenReturn(estudiante);
        when(vehiculoRepository.save(any(Vehiculo.class)))
                .thenAnswer(inv -> {
                    Vehiculo v = inv.getArgument(0);
                    v.setId(99L);
                    return v;
                });

        // ACT
        VehiculoResponse response = vehiculoService.registrar(1L, req);

        // ASSERT
        assertNotNull(response);
        assertEquals("P-456XYZ", response.getPlaca(), "La placa se debe guardar en mayúsculas");
        assertEquals(TipoVehiculo.MOTO, response.getTipo());
        assertEquals("Yamaha", response.getMarca());
        assertEquals("FZ", response.getModelo());
        assertEquals(2024, response.getAnio());
    }

    @Test
    @DisplayName("CP-VEH-03: Registrar vehículo con placa duplicada lanza excepción")
    void testRegistrarVehiculo_PlacaDuplicada() {
        // ARRANGE
        VehiculoRequest req = new VehiculoRequest();
        req.setPlaca("P-123ABC");
        req.setTipo(TipoVehiculo.CARRO);

        when(vehiculoRepository.existsByPlaca("P-123ABC")).thenReturn(true);

        // ACT & ASSERT
        ReglaNegocioException ex = assertThrows(
                ReglaNegocioException.class,
                () -> vehiculoService.registrar(1L, req));

        assertTrue(ex.getMessage().contains("Ya existe un vehículo con la placa"));
    }

    @Test
    @DisplayName("CP-VEH-04: Obtener vehículo por ID existente")
    void testObtenerVehiculoPorId_Existente() {
        // ARRANGE
        Vehiculo v = Vehiculo.builder()
                .id(1L).estudiante(estudiante).placa("P-123ABC")
                .tipo(TipoVehiculo.CARRO).activo(true).build();

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(v));

        // ACT
        Vehiculo result = vehiculoService.obtener(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals("P-123ABC", result.getPlaca());
    }

    @Test
    @DisplayName("CP-VEH-05: Obtener vehículo inexistente lanza excepción")
    void testObtenerVehiculoPorId_NoExistente() {
        // ARRANGE
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> vehiculoService.obtener(999L));

        assertTrue(ex.getMessage().contains("no encontrado"));
    }
}
