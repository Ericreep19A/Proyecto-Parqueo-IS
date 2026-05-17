-- =====================================================================
--  FIX RAPIDO: ajusta tipos TINYINT -> INT para que Hibernate valide bien.
--  Ejecutar UNA sola vez sobre la BD ya creada.
-- =====================================================================
USE parqueo_umg;

ALTER TABLE cursos    MODIFY COLUMN creditos INT NOT NULL DEFAULT 4;
ALTER TABLE semestres MODIFY COLUMN numero   INT NOT NULL;

-- Verificacion
SELECT COLUMN_NAME, DATA_TYPE
FROM   INFORMATION_SCHEMA.COLUMNS
WHERE  TABLE_SCHEMA = 'parqueo_umg'
  AND  ((TABLE_NAME = 'cursos'    AND COLUMN_NAME = 'creditos')
   OR   (TABLE_NAME = 'semestres' AND COLUMN_NAME = 'numero'));
-- Debe mostrar: data_type = 'int' en ambas filas.
