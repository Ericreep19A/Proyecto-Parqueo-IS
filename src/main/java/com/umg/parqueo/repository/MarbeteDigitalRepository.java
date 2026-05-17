package com.umg.parqueo.repository;

import com.umg.parqueo.entity.MarbeteDigital;
import com.umg.parqueo.enums.EstadoMarbete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarbeteDigitalRepository extends JpaRepository<MarbeteDigital, Long> {

    Optional<MarbeteDigital> findByCodigoUnico(String codigoUnico);

    Optional<MarbeteDigital> findByEstudiante_IdAndSemestre_IdAndEstado(
            Long estudianteId, Integer semestreId, EstadoMarbete estado);
}
