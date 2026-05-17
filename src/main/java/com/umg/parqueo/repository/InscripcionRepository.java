package com.umg.parqueo.repository;

import com.umg.parqueo.entity.Inscripcion;
import com.umg.parqueo.enums.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    long countByEstudiante_IdAndSemestre_IdAndEstado(Long estudianteId, Integer semestreId, EstadoInscripcion estado);

    List<Inscripcion> findByEstudiante_IdAndSemestre_Id(Long estudianteId, Integer semestreId);

    /**
     * Analisis de horas de mayor afluencia (RF09).
     * Retorna [hora, cantidad].
     */
    @Query(value = """
            SELECT HOUR(i.horario_salida) AS hora, COUNT(*) AS cantidad
            FROM inscripciones i
            JOIN semestres s ON s.id = i.semestre_id AND s.vigente = TRUE
            WHERE i.estado = 'ACTIVA' AND i.horario_salida IS NOT NULL
            GROUP BY HOUR(i.horario_salida)
            ORDER BY cantidad DESC
            """, nativeQuery = true)
    List<Object[]> obtenerHorasAfluencia();
}
