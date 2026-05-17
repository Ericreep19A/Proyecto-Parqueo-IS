-- =====================================================================
--  DATOS DE PRUEBA / SEED
--  Ejecutar despues de 01_schema.sql
-- =====================================================================
USE parqueo_umg;

-- ---------------------------------------------------------------------
-- Carreras
-- ---------------------------------------------------------------------
INSERT INTO carreras (codigo, nombre, facultad) VALUES
('ISCC','Ingeniería en Sistemas de Información y Ciencias de la Computación','Ingeniería'),
('IIND','Ingeniería Industrial','Ingeniería'),
('ADM','Administración de Empresas','Ciencias Económicas');

-- ---------------------------------------------------------------------
-- Semestres
-- ---------------------------------------------------------------------
INSERT INTO semestres (codigo, anio, numero, fecha_inicio, fecha_fin, vigente) VALUES
('2025-2', 2025, 2, '2025-07-01', '2025-11-30', FALSE),
('2026-1', 2026, 1, '2026-01-15', '2026-06-15', TRUE);

-- ---------------------------------------------------------------------
-- Cursos
-- ---------------------------------------------------------------------
INSERT INTO cursos (codigo, nombre, creditos, carrera_id) VALUES
('IS-501','Ingeniería de Software',          4, 1),
('IS-502','Inteligencia Artificial',         4, 1),
('IS-503','Redes de Computadoras II',        4, 1),
('IS-504','Arquitectura de Computadoras II', 4, 1),
('IS-505','Análisis y Diseño de Sistemas',   4, 1);

-- ---------------------------------------------------------------------
-- Usuarios
--   Password: "Password123."  (hash BCrypt verificado, todos comparten)
--   En produccion usar: BCryptPasswordEncoder().encode(...) por usuario.
-- ---------------------------------------------------------------------
INSERT INTO usuarios (correo, password_hash, rol, activo) VALUES
('brandon.jom@miumg.edu.gt',   '$2b$10$yVP.JTuTWZ6D/Xb39f2OH.0DZ7P1T2Mer2e2oCvRA7qEC6emuE5QC', 'ESTUDIANTE', TRUE),
('erica.hidalgo@miumg.edu.gt', '$2b$10$yVP.JTuTWZ6D/Xb39f2OH.0DZ7P1T2Mer2e2oCvRA7qEC6emuE5QC', 'ESTUDIANTE', TRUE),
('henry.sicajau@miumg.edu.gt', '$2b$10$yVP.JTuTWZ6D/Xb39f2OH.0DZ7P1T2Mer2e2oCvRA7qEC6emuE5QC', 'ESTUDIANTE', TRUE),
('isaura.caceres@miumg.edu.gt','$2b$10$yVP.JTuTWZ6D/Xb39f2OH.0DZ7P1T2Mer2e2oCvRA7qEC6emuE5QC', 'ESTUDIANTE', TRUE),
('seguridad@miumg.edu.gt',     '$2b$10$yVP.JTuTWZ6D/Xb39f2OH.0DZ7P1T2Mer2e2oCvRA7qEC6emuE5QC', 'SEGURIDAD',  TRUE),
('admin@miumg.edu.gt',         '$2b$10$yVP.JTuTWZ6D/Xb39f2OH.0DZ7P1T2Mer2e2oCvRA7qEC6emuE5QC', 'ADMIN',      TRUE);

-- ---------------------------------------------------------------------
-- Estudiantes
-- ---------------------------------------------------------------------
INSERT INTO estudiantes (usuario_id, carne, nombres, apellidos, dpi, telefono, carrera_id, fecha_ingreso) VALUES
(1, '0910-22-3399', 'Brandon Vicente', 'Jom Velasquez',   '1234567890101', '50212345678', 1, '2022-01-15'),
(2, '0910-22-4521', 'Erica Patricia',  'Hidalgo Castro',  '2345678901101', '50223456789', 1, '2022-01-15'),
(3, '0910-22-7812', 'Henry Josue',     'Sicajau Tzalam',  '3456789012101', '50234567890', 1, '2022-01-15'),
(4, '0910-22-6634', 'Isaura Marisel',  'Caceres Perez',   '4567890123101', '50245678901', 1, '2022-01-15');

-- ---------------------------------------------------------------------
-- Inscripciones (todos en el semestre vigente 2026-1)
-- ---------------------------------------------------------------------
INSERT INTO inscripciones (estudiante_id, curso_id, semestre_id, seccion, horario_salida, estado) VALUES
(1, 1, 2, 'A', '20:00:00', 'ACTIVA'),
(1, 2, 2, 'A', '21:00:00', 'ACTIVA'),
(1, 3, 2, 'B', '19:00:00', 'ACTIVA'),
(2, 1, 2, 'A', '20:00:00', 'ACTIVA'),
(2, 5, 2, 'A', '21:00:00', 'ACTIVA'),
(3, 1, 2, 'A', '20:00:00', 'ACTIVA'),
(3, 4, 2, 'A', '19:00:00', 'ACTIVA'),
(4, 2, 2, 'A', '21:00:00', 'ACTIVA'),
(4, 5, 2, 'A', '20:00:00', 'ACTIVA');

-- ---------------------------------------------------------------------
-- Solvencias (todos solventes excepto Henry para probar denegacion)
-- ---------------------------------------------------------------------
INSERT INTO solvencias (estudiante_id, semestre_id, solvente_academico, solvente_financiero, observaciones) VALUES
(1, 2, TRUE,  TRUE,  NULL),
(2, 2, TRUE,  TRUE,  NULL),
(3, 2, TRUE,  FALSE, 'Saldo pendiente en colegiatura'),
(4, 2, TRUE,  TRUE,  NULL);

-- ---------------------------------------------------------------------
-- Pagos de estacionamiento (Brandon, Erica e Isaura)
-- ---------------------------------------------------------------------
INSERT INTO pagos_estacionamiento (estudiante_id, semestre_id, monto, no_recibo, metodo_pago, estado) VALUES
(1, 2, 350.00, 'REC-2026-0001', 'TRANSFERENCIA', 'VIGENTE'),
(2, 2, 350.00, 'REC-2026-0002', 'EFECTIVO',      'VIGENTE'),
(4, 2, 350.00, 'REC-2026-0003', 'TARJETA',       'VIGENTE');
-- Henry intencionalmente sin pago

-- ---------------------------------------------------------------------
-- Marbetes digitales
-- ---------------------------------------------------------------------
INSERT INTO marbetes_digitales (estudiante_id, pago_id, semestre_id, codigo_unico,
                                fecha_vigencia_inicio, fecha_vigencia_fin, estado) VALUES
(1, 1, 2, 'MRB-2026-1-A4F92E11', '2026-01-15', '2026-06-15', 'ACTIVO'),
(2, 2, 2, 'MRB-2026-1-B7C03D52', '2026-01-15', '2026-06-15', 'ACTIVO'),
(4, 3, 2, 'MRB-2026-1-D9E81F73', '2026-01-15', '2026-06-15', 'ACTIVO');

-- ---------------------------------------------------------------------
-- Vehiculos
-- ---------------------------------------------------------------------
INSERT INTO vehiculos (estudiante_id, placa, tipo, marca, modelo, color, anio) VALUES
(1, 'P-123ABC',  'CARRO', 'Toyota',  'Yaris',   'Blanco', 2020),
(1, 'M-456DEF',  'MOTO',  'Honda',   'CB125F',  'Rojo',   2022),
(2, 'P-789GHI',  'CARRO', 'Hyundai', 'Accent',  'Negro',  2019),
(3, 'P-321JKL',  'CARRO', 'Mazda',   '3',       'Gris',   2021),
(4, 'M-654MNO',  'MOTO',  'Yamaha',  'YBR125',  'Azul',   2023);

-- ---------------------------------------------------------------------
-- Espacios de estacionamiento
-- ---------------------------------------------------------------------
INSERT INTO espacios_estacionamiento (codigo, zona, tipo) VALUES
('A-01','A','CARRO'),('A-02','A','CARRO'),('A-03','A','CARRO'),
('A-04','A','CARRO'),('A-05','A','CARRO'),
('B-01','B','CARRO'),('B-02','B','CARRO'),('B-03','B','CARRO'),
('B-04','B','CARRO'),('B-05','B','CARRO'),
('C-01','C','CARRO'),('C-02','C','CARRO'),('C-03','C','CARRO'),
('M-01','MOTOS','MOTO'),('M-02','MOTOS','MOTO'),('M-03','MOTOS','MOTO'),
('M-04','MOTOS','MOTO'),('M-05','MOTOS','MOTO');

-- ---------------------------------------------------------------------
-- Verificacion rapida
-- ---------------------------------------------------------------------
SELECT 'Carreras' AS tabla, COUNT(*) AS total FROM carreras
UNION ALL SELECT 'Usuarios',      COUNT(*) FROM usuarios
UNION ALL SELECT 'Estudiantes',   COUNT(*) FROM estudiantes
UNION ALL SELECT 'Cursos',        COUNT(*) FROM cursos
UNION ALL SELECT 'Inscripciones', COUNT(*) FROM inscripciones
UNION ALL SELECT 'Solvencias',    COUNT(*) FROM solvencias
UNION ALL SELECT 'Pagos',         COUNT(*) FROM pagos_estacionamiento
UNION ALL SELECT 'Marbetes',      COUNT(*) FROM marbetes_digitales
UNION ALL SELECT 'Vehiculos',     COUNT(*) FROM vehiculos
UNION ALL SELECT 'Espacios',      COUNT(*) FROM espacios_estacionamiento;
