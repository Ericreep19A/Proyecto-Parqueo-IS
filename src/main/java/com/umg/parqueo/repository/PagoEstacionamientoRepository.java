package com.umg.parqueo.repository;

import com.umg.parqueo.entity.PagoEstacionamiento;
import com.umg.parqueo.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoEstacionamientoRepository extends JpaRepository<PagoEstacionamiento, Long> {
    Optional<PagoEstacionamiento> findByEstudiante_IdAndSemestre_IdAndEstado(
            Long estudianteId, Integer semestreId, EstadoPago estado);
}
