# Rama DB - Base de Datos del Sistema de Parqueo

## Estructura de Directorios

### `/database/migrations/`
- Contiene scripts de migración de base de datos
- Cada archivo representa un cambio incremental en el esquema
- Archivo: `001_initial_schema.sql` - Define el esquema inicial

### `/database/schemas/`
- Define la estructura completa de tablas y relaciones
- Archivo: `parking_schema.sql` - Esquema principal del sistema

### `/database/scripts/`
- Scripts útiles para operaciones comunes
- `seed_data.sql` - Datos de prueba e inicialización
- `backup.sql` - Procedimientos de backup y recuperación

### `/database/config/`
- Configuración de la conexión y parámetros
- `database.config` - Parámetros de rendimiento y ajustes
- `connection.properties` - Credenciales y conexión

## Tareas Pendientes

### 1. **Tablas Principales** 
   - [ ] Crear tabla de usuarios
   - [ ] Crear tabla de vehículos
   - [ ] Crear tabla de espacios de parqueo
   - [ ] Crear tabla de transacciones

### 2. **Relaciones y Constraints**
   - [ ] Definir relaciones entre tablas
   - [ ] Agregar primary keys
   - [ ] Agregar foreign keys
   - [ ] Definir constraints de validación

### 3. **Índices y Performance**
   - [ ] Crear índices en campos clave
   - [ ] Optimizar queries

### 4. **Datos Iniciales**
   - [ ] Insertar datos de prueba
   - [ ] Configurar datos de referencia

### 5. **Seguridad**
   - [ ] Configurar permisos de usuarios
   - [ ] Definir roles de BD

### 6. **Respaldo y Recuperación**
   - [ ] Configurar procedimientos de backup
   - [ ] Crear scripts de recuperación
