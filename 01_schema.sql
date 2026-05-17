-- =====================================================================
--  PROYECTO: Sistema de Control de Acceso Vehicular - Universidad X
--  MODULO:   Esquema relacional (DDL)
--  MOTOR:    MySQL 8.0+
--  AUTOR:    Brandon Jom (Backend / DBA)
--  FECHA:    Mayo 2026
-- =====================================================================

DROP DATABASE IF EXISTS parqueo_umg;
CREATE DATABASE parqueo_umg
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE parqueo_umg;

-- ---------------------------------------------------------------------
-- 1. USUARIOS  (cuenta de acceso al sistema)
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    correo          VARCHAR(120) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    rol             ENUM('ESTUDIANTE','SEGURIDAD','ADMIN') NOT NULL DEFAULT 'ESTUDIANTE',
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_login    DATETIME     NULL,
    INDEX idx_usuarios_correo (correo)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. CARRERAS
-- ---------------------------------------------------------------------
CREATE TABLE carreras (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(20)  NOT NULL UNIQUE,
    nombre          VARCHAR(150) NOT NULL,
    facultad        VARCHAR(150) NOT NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. ESTUDIANTES  (perfil ligado a usuario)
-- ---------------------------------------------------------------------
CREATE TABLE estudiantes (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    usuario_id      BIGINT       NOT NULL UNIQUE,
    carne           VARCHAR(20)  NOT NULL UNIQUE,
    nombres         VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(100) NOT NULL,
    dpi             VARCHAR(20)  NULL,
    telefono        VARCHAR(20)  NULL,
    carrera_id      INT          NOT NULL,
    fecha_ingreso   DATE         NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_est_usuario  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_est_carrera  FOREIGN KEY (carrera_id) REFERENCES carreras(id),
    INDEX idx_estudiantes_carne (carne)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. SEMESTRES (ciclos académicos)
-- ---------------------------------------------------------------------
CREATE TABLE semestres (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(20)  NOT NULL UNIQUE,    -- ej: 2026-1
    anio            INT          NOT NULL,
    numero          TINYINT      NOT NULL,           -- 1 o 2
    fecha_inicio    DATE         NOT NULL,
    fecha_fin       DATE         NOT NULL,
    vigente         BOOLEAN      NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. CURSOS
-- ---------------------------------------------------------------------
CREATE TABLE cursos (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(20)  NOT NULL UNIQUE,
    nombre          VARCHAR(150) NOT NULL,
    creditos        TINYINT      NOT NULL DEFAULT 4,
    carrera_id      INT          NOT NULL,
    CONSTRAINT fk_curso_carrera FOREIGN KEY (carrera_id) REFERENCES carreras(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. INSCRIPCIONES (estudiante - curso - semestre)
-- ---------------------------------------------------------------------
CREATE TABLE inscripciones (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    estudiante_id   BIGINT       NOT NULL,
    curso_id        INT          NOT NULL,
    semestre_id     INT          NOT NULL,
    seccion         VARCHAR(5)   NOT NULL DEFAULT 'A',
    horario_salida  TIME         NULL,        -- usado para analizar horas pico
    estado          ENUM('ACTIVA','RETIRADA','APROBADA','REPROBADA') NOT NULL DEFAULT 'ACTIVA',
    fecha_inscripcion DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_insc_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE,
    CONSTRAINT fk_insc_curso      FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_insc_semestre   FOREIGN KEY (semestre_id) REFERENCES semestres(id),
    UNIQUE KEY uk_insc_unica (estudiante_id, curso_id, semestre_id, seccion)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. SOLVENCIA ACADEMICA Y FINANCIERA
--    Se mantiene un registro por semestre para auditoría.
-- ---------------------------------------------------------------------
CREATE TABLE solvencias (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    estudiante_id   BIGINT       NOT NULL,
    semestre_id     INT          NOT NULL,
    solvente_academico   BOOLEAN NOT NULL DEFAULT TRUE,
    solvente_financiero  BOOLEAN NOT NULL DEFAULT TRUE,
    observaciones        VARCHAR(500) NULL,
    fecha_actualizacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                         ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_solv_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE,
    CONSTRAINT fk_solv_semestre   FOREIGN KEY (semestre_id) REFERENCES semestres(id),
    UNIQUE KEY uk_solv_unico (estudiante_id, semestre_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. VEHICULOS  (un estudiante puede tener varios)
-- ---------------------------------------------------------------------
CREATE TABLE vehiculos (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    estudiante_id   BIGINT       NOT NULL,
    placa           VARCHAR(15)  NOT NULL UNIQUE,
    tipo            ENUM('CARRO','MOTO') NOT NULL,
    marca           VARCHAR(50)  NULL,
    modelo          VARCHAR(50)  NULL,
    color           VARCHAR(30)  NULL,
    anio            INT          NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_registro  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_veh_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE,
    INDEX idx_vehiculos_placa (placa)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. PAGOS DE ESTACIONAMIENTO (semestrales)
-- ---------------------------------------------------------------------
CREATE TABLE pagos_estacionamiento (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    estudiante_id   BIGINT       NOT NULL,
    semestre_id     INT          NOT NULL,
    monto           DECIMAL(10,2) NOT NULL,
    no_recibo       VARCHAR(50)  NOT NULL UNIQUE,
    fecha_pago      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_pago     ENUM('EFECTIVO','TRANSFERENCIA','TARJETA','BOLETA') NOT NULL,
    estado          ENUM('VIGENTE','ANULADO','VENCIDO') NOT NULL DEFAULT 'VIGENTE',
    CONSTRAINT fk_pago_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE,
    CONSTRAINT fk_pago_semestre   FOREIGN KEY (semestre_id) REFERENCES semestres(id),
    UNIQUE KEY uk_pago_unico (estudiante_id, semestre_id, estado)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. MARBETES DIGITALES
--     Un marbete por estudiante por semestre, ligado a un pago vigente.
-- ---------------------------------------------------------------------
CREATE TABLE marbetes_digitales (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    estudiante_id   BIGINT       NOT NULL,
    pago_id         BIGINT       NOT NULL,
    semestre_id     INT          NOT NULL,
    codigo_unico    VARCHAR(50)  NOT NULL UNIQUE,    -- UUID
    fecha_emision   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vigencia_inicio DATE   NOT NULL,
    fecha_vigencia_fin    DATE   NOT NULL,
    estado          ENUM('ACTIVO','VENCIDO','REVOCADO') NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT fk_marb_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE,
    CONSTRAINT fk_marb_pago       FOREIGN KEY (pago_id) REFERENCES pagos_estacionamiento(id),
    CONSTRAINT fk_marb_semestre   FOREIGN KEY (semestre_id) REFERENCES semestres(id),
    INDEX idx_marb_codigo (codigo_unico)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 11. ESPACIOS DE ESTACIONAMIENTO
-- ---------------------------------------------------------------------
CREATE TABLE espacios_estacionamiento (
    id              INT          AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(10)  NOT NULL UNIQUE,    -- ej: A-01, M-12
    zona            VARCHAR(20)  NOT NULL,            -- A, B, C, MOTOS
    tipo            ENUM('CARRO','MOTO') NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 12. REGISTROS DE ACCESO (bitácora de ingresos)
-- ---------------------------------------------------------------------
CREATE TABLE registros_acceso (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    estudiante_id   BIGINT       NOT NULL,
    vehiculo_id     BIGINT       NOT NULL,
    marbete_id      BIGINT       NULL,
    espacio_sugerido_id INT      NULL,
    fecha_hora      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resultado       ENUM('AUTORIZADO','DENEGADO') NOT NULL,
    motivo_denegacion VARCHAR(255) NULL,
    qr_token        VARCHAR(255) NULL,
    validado_por    BIGINT       NULL,    -- usuario seguridad (futuro)
    CONSTRAINT fk_reg_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_vehiculo   FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id),
    CONSTRAINT fk_reg_marbete    FOREIGN KEY (marbete_id) REFERENCES marbetes_digitales(id),
    CONSTRAINT fk_reg_espacio    FOREIGN KEY (espacio_sugerido_id) REFERENCES espacios_estacionamiento(id),
    CONSTRAINT fk_reg_seguridad  FOREIGN KEY (validado_por) REFERENCES usuarios(id),
    INDEX idx_reg_fecha (fecha_hora),
    INDEX idx_reg_estudiante (estudiante_id)
) ENGINE=InnoDB;

-- =====================================================================
-- VISTAS PARA REPORTERIA / KPIs
-- =====================================================================

-- Estudiantes con estado consolidado de elegibilidad
CREATE OR REPLACE VIEW v_estudiantes_elegibles AS
SELECT
    e.id              AS estudiante_id,
    e.carne,
    CONCAT(e.nombres,' ',e.apellidos) AS nombre_completo,
    u.correo,
    s.codigo          AS semestre,
    COALESCE(sol.solvente_academico, FALSE)  AS solv_academico,
    COALESCE(sol.solvente_financiero, FALSE) AS solv_financiero,
    (CASE WHEN pe.id IS NOT NULL AND pe.estado='VIGENTE' THEN TRUE ELSE FALSE END) AS pago_vigente,
    (CASE WHEN md.id IS NOT NULL AND md.estado='ACTIVO'
              AND CURDATE() BETWEEN md.fecha_vigencia_inicio AND md.fecha_vigencia_fin
          THEN TRUE ELSE FALSE END) AS marbete_vigente
FROM estudiantes e
JOIN usuarios   u ON u.id = e.usuario_id
JOIN semestres  s ON s.vigente = TRUE
LEFT JOIN solvencias            sol ON sol.estudiante_id = e.id AND sol.semestre_id = s.id
LEFT JOIN pagos_estacionamiento pe  ON pe.estudiante_id  = e.id AND pe.semestre_id  = s.id AND pe.estado='VIGENTE'
LEFT JOIN marbetes_digitales    md  ON md.estudiante_id  = e.id AND md.semestre_id  = s.id AND md.estado='ACTIVO';

-- Análisis de horas de mayor afluencia (RF09)
-- Se basa en los horarios de salida de las inscripciones activas del semestre vigente.
CREATE OR REPLACE VIEW v_horas_afluencia AS
SELECT
    HOUR(i.horario_salida) AS hora,
    COUNT(*)               AS cantidad_estudiantes
FROM inscripciones i
JOIN semestres s ON s.id = i.semestre_id AND s.vigente = TRUE
WHERE i.estado = 'ACTIVA' AND i.horario_salida IS NOT NULL
GROUP BY HOUR(i.horario_salida)
ORDER BY cantidad_estudiantes DESC;

-- Espacios disponibles aproximados (no reserva, sólo sugerencia)
CREATE OR REPLACE VIEW v_espacios_disponibles AS
SELECT
    ee.id,
    ee.codigo,
    ee.zona,
    ee.tipo,
    (SELECT COUNT(*) FROM registros_acceso ra
       WHERE ra.espacio_sugerido_id = ee.id
         AND ra.resultado = 'AUTORIZADO'
         AND DATE(ra.fecha_hora) = CURDATE()) AS asignaciones_hoy
FROM espacios_estacionamiento ee
WHERE ee.activo = TRUE;
