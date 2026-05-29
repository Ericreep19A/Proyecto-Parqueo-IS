package com.umg.parqueo.service;

import com.umg.parqueo.dto.request.LoginRequest;
import com.umg.parqueo.dto.response.LoginResponse;
import com.umg.parqueo.entity.Carrera;
import com.umg.parqueo.entity.Estudiante;
import com.umg.parqueo.entity.Usuario;
import com.umg.parqueo.enums.Rol;
import com.umg.parqueo.repository.EstudianteRepository;
import com.umg.parqueo.repository.UsuarioRepository;
import com.umg.parqueo.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para AuthService.
 *
 * Funcionalidad: Autenticación de usuarios con JWT.
 * RF cubiertos: Autenticación (RNF-03).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AuthService (Autenticación JWT)")
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Estudiante estudiante;

    @BeforeEach
    void setUp() {
        // Configurar el campo @Value("${app.jwt.expiration-ms}")
        ReflectionTestUtils.setField(authService, "expirationMs", 3600000L);

        usuario = Usuario.builder()
                .id(1L)
                .correo("brandon.jom@miumg.edu.gt")
                .passwordHash("$2b$10$yVP.JTuTWZ6D/Xb39f2OH.0DZ7P1T2Mer2e2oCvRA7qEC6emuE5QC")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();

        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");

        estudiante = Estudiante.builder()
                .id(1L)
                .usuario(usuario)
                .carne("0910-22-3399")
                .nombres("Brandon Vicente")
                .apellidos("Jom Velásquez")
                .carrera(carrera)
                .fechaIngreso(LocalDate.of(2022, 1, 1))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CP-AUTH-01: Login exitoso con credenciales válidas")
    void testLoginExitoso() {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setCorreo("brandon.jom@miumg.edu.gt");
        request.setPassword("Password123.");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "brandon.jom@miumg.edu.gt", "Password123.");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioRepository.findByCorreo("brandon.jom@miumg.edu.gt"))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(tokenProvider.generarToken(usuario))
                .thenReturn("eyJhbGciOiJIUzI1NiJ9.test.token");
        when(estudianteRepository.findByUsuario_Id(usuario.getId()))
                .thenReturn(Optional.of(estudiante));

        // ACT
        LoginResponse response = authService.login(request);

        // ASSERT
        assertNotNull(response, "La respuesta no debe ser nula");
        assertNotNull(response.getToken(), "El token debe generarse");
        assertEquals("Bearer", response.getTipo());
        assertEquals("brandon.jom@miumg.edu.gt", response.getCorreo());
        assertEquals("ESTUDIANTE", response.getRol());
        assertEquals("Brandon Vicente Jom Velásquez", response.getNombreCompleto());
        assertEquals(3600L, response.getExpiraEnSegundos());
        verify(usuarioRepository, times(1)).save(any(Usuario.class)); // se actualiza ultimoLogin
    }

    @Test
    @DisplayName("CP-AUTH-02: Login fallido con credenciales incorrectas")
    void testLoginFallido_CredencialesIncorrectas() {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setCorreo("brandon.jom@miumg.edu.gt");
        request.setPassword("PasswordIncorrecta");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // ACT & ASSERT
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request),
                "Debe lanzar BadCredentialsException"
        );

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("CP-AUTH-03: Login exitoso pero sin estudiante asociado (usa correo como nombre)")
    void testLoginSinEstudianteAsociado() {
        // ARRANGE
        Usuario admin = Usuario.builder()
                .id(99L)
                .correo("admin@miumg.edu.gt")
                .passwordHash("hash")
                .rol(Rol.ADMIN)
                .activo(true)
                .build();

        LoginRequest request = new LoginRequest();
        request.setCorreo("admin@miumg.edu.gt");
        request.setPassword("Admin123.");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin@miumg.edu.gt", "Admin123.");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioRepository.findByCorreo("admin@miumg.edu.gt"))
                .thenReturn(Optional.of(admin));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(admin);
        when(tokenProvider.generarToken(admin))
                .thenReturn("eyJhbGciOiJIUzI1NiJ9.admin.token");
        when(estudianteRepository.findByUsuario_Id(admin.getId()))
                .thenReturn(Optional.empty());

        // ACT
        LoginResponse response = authService.login(request);

        // ASSERT
        assertNotNull(response);
        assertEquals("ADMIN", response.getRol());
        // Sin estudiante, debe usar el correo como nombreCompleto
        assertEquals("admin@miumg.edu.gt", response.getNombreCompleto());
    }
}
