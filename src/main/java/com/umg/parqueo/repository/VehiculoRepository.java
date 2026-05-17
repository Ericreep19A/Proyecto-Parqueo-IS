package com.umg.parqueo.repository;

import com.umg.parqueo.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    List<Vehiculo> findByEstudiante_IdAndActivoTrue(Long estudianteId);
    Optional<Vehiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
}
