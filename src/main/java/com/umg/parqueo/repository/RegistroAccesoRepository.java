package com.umg.parqueo.repository;

import com.umg.parqueo.entity.RegistroAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {
    List<RegistroAcceso> findByEstudiante_IdOrderByFechaHoraDesc(Long estudianteId);
    List<RegistroAcceso> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}
