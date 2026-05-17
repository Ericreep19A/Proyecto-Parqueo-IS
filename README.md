Sistema Inteligente de Control de Acceso Vehicular — Parqueo UMG
Descripción General
El presente proyecto corresponde al desarrollo de un sistema inteligente de control de acceso vehicular orientado a la gestión del parqueo universitario de la Universidad Mariano Gálvez de Guatemala (UMG). El sistema fue diseñado con el objetivo de automatizar el proceso de validación de ingreso de estudiantes y vehículos autorizados mediante el uso de tecnologías modernas como códigos QR, autenticación JWT, generación de marbetes digitales y validación automática de solvencias.
La solución tecnológica permite optimizar los procesos de ingreso al parqueo universitario, reduciendo tiempos de espera, mejorando la seguridad institucional y facilitando la administración del acceso vehicular.
El proyecto se encuentra dividido en dos componentes principales:
•	Backend API REST desarrollado con Spring Boot.
•	Frontend Web desarrollado con React.
Además, el sistema utiliza una base de datos MySQL para el almacenamiento de toda la información académica, administrativa y vehicular.
________________________________________
Objetivos del Proyecto
Objetivo General
Diseñar e implementar un sistema inteligente de control de acceso vehicular que permita validar automáticamente el ingreso de estudiantes y vehículos autorizados dentro del parqueo universitario mediante tecnologías modernas de autenticación y validación.
Objetivos Específicos
•	Automatizar el proceso de validación de estudiantes.
•	Validar solvencias académicas y financieras.
•	Verificar el pago del estacionamiento.
•	Generar marbetes digitales.
•	Implementar autenticación segura mediante JWT.
•	Generar y validar códigos QR.
•	Sugerir espacios disponibles de estacionamiento.
•	Mostrar información de afluencia vehicular.
•	Mejorar la seguridad y trazabilidad de los accesos.
________________________________________
Arquitectura del Sistema
El sistema sigue una arquitectura cliente-servidor compuesta por:
Frontend (React + Vite)
        ↓
API REST (Spring Boot)
        ↓
Base de Datos MySQL
Componentes Principales
Frontend
Interfaz gráfica encargada de:
•	Inicio de sesión.
•	Gestión de vehículos.
•	Visualización de solvencias.
•	Consulta de espacios disponibles.
•	Generación y visualización de códigos QR.
•	Gestión de marbetes digitales.
•	Consumo de la API REST.
Backend
Servidor encargado de:
•	Lógica de negocio.
•	Seguridad y autenticación.
•	Validación de reglas del sistema.
•	Generación de QR.
•	Validación de accesos.
•	Gestión de usuarios.
•	Gestión de vehículos.
•	Gestión de solvencias.
•	Gestión de espacios.
•	Exposición de endpoints REST.
Base de Datos
Almacena:
•	Usuarios.
•	Estudiantes.
•	Vehículos.
•	Pagos.
•	Solvencias.
•	Marbetes.
•	Espacios de parqueo.
•	Accesos.
•	Carreras.
•	Cursos.
•	Inscripciones.
________________________________________
Tecnologías Utilizadas
Backend
Tecnología	Descripción
Java 17	Lenguaje principal del backend
Spring Boot 3.3	Framework principal
Spring Security	Seguridad y autenticación
JWT	Autenticación basada en tokens
Spring Data JPA	Persistencia de datos
MySQL	Base de datos relacional
Maven	Gestión de dependencias
Swagger/OpenAPI	Documentación de API
ZXing	Generación y lectura de códigos QR
Lombok	Reducción de código repetitivo
Frontend
Tecnología	Descripción
React	Librería principal del frontend
Vite	Herramienta de compilación
JavaScript	Lenguaje principal
HTML5	Estructura de interfaces
CSS3	Estilos visuales
Node.js	Entorno de ejecución
npm	Gestión de paquetes
Base de Datos
Tecnología	Descripción
MySQL 8	Sistema gestor de base de datos
MySQL Workbench	Administración de la BD
________________________________________
Estructura General del Proyecto
Proyecto/
│
├── parqueo-backend/
│   ├── database/
│   ├── src/
│   ├── pom.xml
│   └── README.md
│
├── parqueo-frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
└── database/
    ├── 01_schema.sql
    ├── 02_data.sql
    └── 03_fix_tipos.sql
________________________________________
Base de Datos
Nombre de la Base de Datos
parqueo_umg
Contenido de la Base de Datos
La base de datos incluye:
•	12 tablas principales.
•	Relaciones mediante llaves foráneas.
•	Datos de prueba.
•	Vistas para consultas optimizadas.
Archivos SQL
Archivo	Descripción
01_schema.sql	Creación de tablas y estructura
02_data.sql	Inserción de datos de prueba
03_fix_tipos.sql	Correcciones de tipos de datos
________________________________________
Instalación del Proyecto
Requisitos Previos
Antes de ejecutar el sistema, es necesario instalar:
Herramienta	Versión Recomendada
JDK	17
Maven	3.9+
Node.js	18+
npm	9+
MySQL	8+
Git	Última versión
VS Code	Última versión
________________________________________
Configuración de la Base de Datos
Paso 1 — Crear la Base de Datos
Abrir MySQL Workbench y ejecutar:
01_schema.sql
Paso 2 — Insertar Datos
Ejecutar:
02_data.sql
Paso 3 — Correcciones
Ejecutar:
03_fix_tipos.sql
________________________________________
Configuración del Backend
Paso 1 — Abrir el Proyecto
Abrir la carpeta:
parqueo-backend
Paso 2 — Configurar Credenciales
Editar:
src/main/resources/application.properties
Configurar:
spring.datasource.url=jdbc:mysql://localhost:3306/parqueo_umg
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
Paso 3 — Instalar Dependencias
mvn clean install
Paso 4 — Ejecutar Backend
mvn spring-boot:run
URLs Importantes
Servicio	URL
API REST	http://localhost:8080/api

Swagger UI	http://localhost:8080/api/swagger-ui.html

________________________________________
Configuración del Frontend
Paso 1 — Abrir Proyecto Frontend
Abrir la carpeta:
parqueo-frontend
Paso 2 — Instalar Dependencias
npm install
Paso 3 — Configurar Variables de Entorno
Crear archivo:
.env
Ejemplo:
VITE_API_URL=http://localhost:8080/api
Paso 4 — Ejecutar Frontend
npm run dev
URL Frontend
http://localhost:5173
________________________________________
Seguridad del Sistema
El sistema implementa múltiples mecanismos de seguridad:
JWT Authentication
El backend utiliza JSON Web Tokens para:
•	Autenticar usuarios.
•	Validar sesiones.
•	Proteger endpoints.
•	Controlar roles.
Roles Implementados
Rol	Descripción
ADMIN	Administración general
SEGURIDAD	Validación de accesos
ESTUDIANTE	Uso del sistema
Seguridad Implementada
•	Endpoints protegidos.
•	Tokens Bearer.
•	Validación de permisos.
•	Arquitectura stateless.
•	Protección mediante Spring Security.
________________________________________
Funcionalidades Principales
Gestión de Usuarios
El sistema permite:
•	Inicio de sesión.
•	Validación de credenciales.
•	Gestión de roles.
•	Consulta de perfil.
________________________________________
🚘 Gestión de Vehículos
Los estudiantes pueden:
•	Registrar vehículos.
•	Consultar vehículos registrados.
•	Validar vehículos autorizados.
________________________________________
Marbete Digital
El sistema genera marbetes digitales asociados a:
•	Estudiante.
•	Vehículo.
•	Vigencia.
Características:
•	Validación automática.
•	Generación digital.
•	Asociación con QR.
________________________________________
Generación de Código QR
El sistema genera códigos QR para:
•	Identificación del estudiante.
•	Validación de acceso.
•	Control de ingreso vehicular.
Tecnología utilizada:
ZXing
________________________________________
Validación de Acceso
Antes de permitir el ingreso al parqueo, el sistema valida:
•	Existencia del estudiante.
•	Inscripción activa.
•	Solvencia académica.
•	Solvencia financiera.
•	Pago vigente.
•	Vigencia del marbete.
•	Validez del QR.
Si alguna condición falla:
ACCESO DENEGADO
________________________________________
Gestión de Espacios
El sistema permite:
•	Consultar espacios disponibles.
•	Sugerir espacios.
•	Consultar horas de mayor afluencia.
________________________________________
API REST
Login
POST /api/auth/login
Ejemplo:
{
  "correo": "brandon.jom@miumg.edu.gt",
  "password": "Password123."
}
________________________________________
 Endpoints Disponibles
Método	Endpoint	Descripción
POST	/auth/login	Inicio de sesión
GET	/estudiantes/perfil	Perfil del estudiante
GET	/estudiantes/mi-solvencia	Estado de solvencia
GET	/vehiculos/mis-vehiculos	Vehículos registrados
POST	/vehiculos	Registrar vehículo
GET	/marbetes/mi-marbete	Consultar marbete
POST	/marbetes/generar	Generar marbete
GET	/qr/mi-qr	Obtener QR
POST	/accesos/validar	Validar ingreso
GET	/espacios/disponibles	Espacios disponibles
GET	/espacios/sugerir	Sugerencia de espacio
GET	/espacios/horas-afluencia	Estadísticas
________________________________________
 Flujo General de Uso
Flujo de Acceso Vehicular
1.	El estudiante inicia sesión.
2.	El sistema valida credenciales.
3.	El usuario genera QR.
4.	Seguridad escanea el QR.
5.	El backend valida:
o	Solvencia.
o	Pago.
o	Marbete.
o	Vehículo.
6.	El sistema autoriza o deniega acceso.
7.	Se sugiere un espacio disponible.
________________________________________
 Pruebas del Sistema
El proyecto incluye:
•	Validaciones backend.
•	Seguridad JWT.
•	Validaciones de acceso.
•	Consultas de solvencia.
•	Generación de QR.
•	Integración API REST.
Ejecutar Pruebas
mvn test
________________________________________
Requerimientos Cubiertos
Código	Requerimiento
RF01	Registro de estudiantes inscritos
RF02	Validación de solvencia
RF03	Validación de pagos
RF04	Generación de marbetes
RF05	Validación de vigencia
RF06	Generación de QR
RF07	Denegación automática
RF08	Sugerencia de espacios
RF09	Horas de afluencia
RF10	Consulta de solvencia
________________________________________
Ventajas del Sistema
•	Automatización del ingreso vehicular.
•	Mayor seguridad institucional.
•	Reducción de tiempos de espera.
•	Mejor control administrativo.
•	Validaciones automáticas.
•	Escalabilidad.
•	Arquitectura moderna.
•	Integración sencilla.
________________________________________
Mejoras Futuras
Funcionalidades Futuras
•	Aplicación móvil.
•	Integración con cámaras.
•	Reconocimiento de placas.
•	Sensores inteligentes.
•	Dashboard administrativo.
•	Reportes estadísticos.
•	Notificaciones automáticas.
•	Integración con pagos en línea.
•	Historial de accesos.
________________________________________
Equipo de Desarrollo
Área	Responsable
Backend y Base de Datos	Brandon Vicente Jom Velásquez
Frontend	Equipo de Desarrollo
Base de Datos	Equipo DBA
Documentación	Equipo del Proyecto
________________________________________
Institución
Universidad Mariano Gálvez de Guatemala
Facultad de Ingeniería en Sistemas
________________________________________
Licencia
Este proyecto fue desarrollado con fines académicos y educativos.
________________________________________
Contacto
Soporte Técnico
Correo institucional:
brandon.jom@miumg.edu.gt
________________________________________
Estado del Proyecto
MVP FUNCIONAL COMPLETADO
Características implementadas:
 Backend REST funcional
 Base de datos estructurada
 Seguridad JWT
 Gestión de vehículos
 Generación de QR
 Validación de acceso
 Marbetes digitales
 Sugerencia de espacios
 Documentación Swagger
 Integración con MySQL
 Frontend React
________________________________________
Conclusión
El sistema inteligente de control de acceso vehicular representa una solución tecnológica moderna orientada a optimizar la administración del parqueo universitario. Mediante el uso de autenticación segura, validaciones automáticas, generación de códigos QR y gestión digital de marbetes, la plataforma mejora significativamente la seguridad, eficiencia y control institucional.
La arquitectura implementada permite futuras ampliaciones y facilita la integración con nuevas tecnologías relacionadas con automatización, monitoreo y control vehicular inteligente.

