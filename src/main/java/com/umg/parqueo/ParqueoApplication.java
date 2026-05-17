package com.umg.parqueo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sistema de Control de Acceso Vehicular - Universidad Mariano Gálvez.
 *
 * Punto de entrada de la aplicación Spring Boot.
 * - API REST en http://localhost:8080/api
 * - Swagger UI en http://localhost:8080/api/swagger-ui.html
 */
@SpringBootApplication
public class ParqueoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParqueoApplication.class, args);
    }
}
