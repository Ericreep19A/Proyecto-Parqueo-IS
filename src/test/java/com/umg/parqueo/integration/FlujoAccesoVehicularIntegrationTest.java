package com.umg.parqueo.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umg.parqueo.dto.request.LoginRequest;
import com.umg.parqueo.dto.request.ValidarQrRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PRUEBA DE INTEGRACIÓN END-TO-END
 *
 * Valida el flujo completo del sistema con todos los componentes:
 * Spring Boot + Spring Security + JWT + MySQL + Hibernate + ZXing.
 *
 * Flujo del MVP probado:
 *   1. Login con JWT (AuthController)
 *   2. Consulta de perfil (EstudianteController)
 *   3. Consulta de solvencia consolidada (RF02, RF03, RF05, RF07, RF10)
 *   4. Consulta de marbete vigente (RF04, RF05)
 *   5. Generación de QR firmado (RF06)
 *   6. Validación de acceso completo: QR + vehículo + elegibilidad (RF06, RF07, RF08)
 *   7. Caso negativo: estudiante NO solvente (Henry) - DENEGADO
 *
 * PRECONDICIONES:
 *   - MySQL corriendo en localhost:3306 con la BD parqueo_umg
 *   - Datos cargados desde 02_data.sql
 *   - Usuario Brandon: solvente (carné 0910-22-3399)
 *   - Usuario Henry: NO solvente (sin pago de estacionamiento)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Prueba de Integración E2E - Flujo Completo de Acceso Vehicular")
public class FlujoAccesoVehicularIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Estado compartido entre pasos del flujo
    private static String tokenBrandon;
    private static String codigoMarbete;
    private static String qrToken;
    private static Long vehiculoId;

    @Test
    @Order(1)
    @DisplayName("PASO 1: Login exitoso de Brandon - obtener JWT")
    void paso1_LoginBrandon() throws Exception {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setCorreo("brandon.jom@miumg.edu.gt");
        request.setPassword("Password123.");

        // ACT
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.correo").value("brandon.jom@miumg.edu.gt"))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"))
                .andReturn();

        // ASSERT - capturar token
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        tokenBrandon = body.get("token").asText();

        assertNotNull(tokenBrandon, "Token JWT debe generarse");
        System.out.println("✅ PASO 1 OK - Token Brandon: " + tokenBrandon.substring(0, 40) + "...");
    }

    @Test
    @Order(2)
    @DisplayName("PASO 2: Consultar perfil de Brandon")
    void paso2_ConsultarPerfil() throws Exception {
        mockMvc.perform(get("/estudiantes/perfil")
                        .header("Authorization", "Bearer " + tokenBrandon))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carne").value("0910-22-3399"))
                .andExpect(jsonPath("$.nombreCompleto").value("Brandon Vicente Jom Velasquez"));

        System.out.println("✅ PASO 2 OK - Perfil de Brandon obtenido");
    }

    @Test
    @Order(3)
    @DisplayName("PASO 3: Consultar solvencia consolidada (RF02, RF03, RF05, RF10)")
    void paso3_ConsultarSolvencia() throws Exception {
        mockMvc.perform(get("/estudiantes/mi-solvencia")
                        .header("Authorization", "Bearer " + tokenBrandon))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carne").value("0910-22-3399"))
                .andExpect(jsonPath("$.inscritoEnSemestre").value(true))
                .andExpect(jsonPath("$.solventeAcademico").value(true))
                .andExpect(jsonPath("$.solventeFinanciero").value(true))
                .andExpect(jsonPath("$.pagoEstacionamientoVigente").value(true))
                .andExpect(jsonPath("$.marbeteVigente").value(true))
                .andExpect(jsonPath("$.autorizadoIngreso").value(true));

        System.out.println("✅ PASO 3 OK - Brandon es solvente y autorizado");
    }

    @Test
    @Order(4)
    @DisplayName("PASO 4: Consultar marbete vigente (RF04, RF05)")
    void paso4_ConsultarMarbete() throws Exception {
        MvcResult result = mockMvc.perform(get("/marbetes/mi-marbete")
                        .header("Authorization", "Bearer " + tokenBrandon))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoUnico").exists())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.vigente").value(true))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        codigoMarbete = body.get("codigoUnico").asText();

        assertNotNull(codigoMarbete);
        System.out.println("✅ PASO 4 OK - Marbete: " + codigoMarbete);
    }

    @Test
    @Order(5)
    @DisplayName("PASO 5: Generar código QR firmado (RF06)")
    void paso5_GenerarQR() throws Exception {
        MvcResult result = mockMvc.perform(get("/qr/mi-qr")
                        .header("Authorization", "Bearer " + tokenBrandon))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qrToken").exists())
                .andExpect(jsonPath("$.qrImagenBase64").exists())
                .andExpect(jsonPath("$.emitidoEn").exists())
                .andExpect(jsonPath("$.expiraEn").exists())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        qrToken = body.get("qrToken").asText();

        assertNotNull(qrToken);
        assertTrue(qrToken.startsWith("eyJ"), "El QR token debe ser un JWT (inicia con eyJ)");
        System.out.println("✅ PASO 5 OK - QR firmado generado (expira en 5 min)");
    }

    @Test
    @Order(6)
    @DisplayName("PASO 6: Listar vehículos para obtener vehiculoId")
    void paso6_ListarVehiculos() throws Exception {
        MvcResult result = mockMvc.perform(get("/vehiculos/mis-vehiculos")
                        .header("Authorization", "Bearer " + tokenBrandon))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andReturn();

        JsonNode arr = objectMapper.readTree(result.getResponse().getContentAsString());
        vehiculoId = arr.get(0).get("id").asLong();

        assertNotNull(vehiculoId);
        System.out.println("✅ PASO 6 OK - Vehículo ID: " + vehiculoId);
    }

    @Test
    @Order(7)
    @DisplayName("PASO 7: Validar acceso al estacionamiento - AUTORIZADO (RF06, RF07, RF08)")
    void paso7_ValidarAcceso_Autorizado() throws Exception {
        ValidarQrRequest req = new ValidarQrRequest();
        req.setQrToken(qrToken);
        req.setVehiculoId(vehiculoId);

        mockMvc.perform(post("/accesos/validar")
                        .header("Authorization", "Bearer " + tokenBrandon)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("AUTORIZADO"))
                .andExpect(jsonPath("$.mensaje").value("Ingreso autorizado"))
                .andExpect(jsonPath("$.carne").value("0910-22-3399"))
                .andExpect(jsonPath("$.placaVehiculo").exists())
                .andExpect(jsonPath("$.espacioSugerido").exists())
                .andExpect(jsonPath("$.fechaHora").exists())
                .andExpect(jsonPath("$.registroId").exists());

        System.out.println("✅ PASO 7 OK - Acceso AUTORIZADO y registrado en auditoría");
    }

    @Test
    @Order(8)
    @DisplayName("PASO 8: Caso negativo - Henry NO solvente, DENEGADO (RF07)")
    void paso8_AccesoDenegado_Henry() throws Exception {
        // Login con Henry (sin pago de estacionamiento)
        LoginRequest loginHenry = new LoginRequest();
        loginHenry.setCorreo("henry.sicajau@miumg.edu.gt");
        loginHenry.setPassword("Password123.");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginHenry)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String tokenHenry = body.get("token").asText();

        // Consultar solvencia de Henry - debe estar NO autorizado
        mockMvc.perform(get("/estudiantes/mi-solvencia")
                        .header("Authorization", "Bearer " + tokenHenry))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autorizadoIngreso").value(false))
                .andExpect(jsonPath("$.pagoEstacionamientoVigente").value(false));

        System.out.println("✅ PASO 8 OK - Henry correctamente DENEGADO");
        System.out.println();
        System.out.println("🎉 ============================================");
        System.out.println("🎉 FLUJO COMPLETO EXITOSO");
        System.out.println("🎉 Todos los componentes funcionan correctamente:");
        System.out.println("🎉   - Spring Security + JWT");
        System.out.println("🎉   - Spring Data JPA + MySQL");
        System.out.println("🎉   - Generación de QR con ZXing");
        System.out.println("🎉   - Validación de solvencia (RF02, RF03, RF05, RF07, RF10)");
        System.out.println("🎉   - Validación de acceso (RF06, RF07, RF08)");
        System.out.println("🎉 ============================================");
    }
}
