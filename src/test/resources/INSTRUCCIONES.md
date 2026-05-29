# Pruebas del Sistema de Control de Acceso Vehicular

## Estructura de Tests

```
src/test/java/com/umg/parqueo/
├── service/                         # Pruebas Unitarias (JUnit 5 + Mockito)
│   ├── AuthServiceTest.java         # Login JWT (3 tests)
│   ├── EstudianteServiceTest.java   # Solvencia (6 tests)
│   ├── MarbeteServiceTest.java      # Marbete digital (6 tests)
│   ├── QrServiceTest.java           # Generación/validación QR (5 tests)
│   ├── AccesoServiceTest.java       # Validación de acceso (5 tests)
│   ├── EspacioServiceTest.java      # Sugerencia/Afluencia (4 tests)
│   └── VehiculoServiceTest.java     # Gestión vehículos (5 tests)
│
└── integration/
    └── FlujoAccesoVehicularIntegrationTest.java  # E2E completo (8 pasos)
```

## Cobertura

| Servicio | Tests | RF Cubiertos |
|----------|-------|--------------|
| AuthService | 3 | Autenticación JWT (RNF-03) |
| EstudianteService | 6 | RF01, RF02, RF03, RF05, RF07, RF10 |
| MarbeteService | 6 | RF04, RF05 |
| QrService | 5 | RF06 |
| AccesoService | 5 | RF06, RF07, RF08 |
| EspacioService | 4 | RF08, RF09 |
| VehiculoService | 5 | Gestión vehículos |
| **TOTAL UNITARIO** | **34** | Todos los RF |
| Integration Test | 8 pasos | Flujo completo del MVP |

## Comandos para Ejecutar

```bash
# Solo pruebas unitarias (RÁPIDO - no necesita BD)
mvn test -Dtest="*ServiceTest"

# Solo prueba de integración (requiere MySQL corriendo)
mvn test -Dtest="FlujoAccesoVehicularIntegrationTest"

# Todas las pruebas
mvn test

# Generar reporte HTML (después de mvn test)
mvn surefire-report:report
# Ver: target/site/surefire-report.html

# Limpiar y ejecutar
mvn clean test
```

## Precondiciones para el Integration Test

- MySQL corriendo en localhost:3306
- BD `parqueo_umg` creada con los scripts `01_schema.sql` y `02_data.sql`
- Spring Boot configurado con `application.properties`

## Resultados Esperados

```
[INFO] Tests run: 42, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
