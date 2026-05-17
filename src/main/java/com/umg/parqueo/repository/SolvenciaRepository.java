package com.umg.parqueo.repository;

import com.umg.parqueo.entity.Solvencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolvenciaRepository extends JpaRepository<Solvencia, Long> {
    Optional<Solvencia> findByEstudiante_IdAndSemestre_Id(Long estudianteId, Integer semestreId);
}
