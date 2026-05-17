package com.umg.parqueo.repository;

import com.umg.parqueo.entity.EspacioEstacionamiento;
import com.umg.parqueo.enums.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspacioEstacionamientoRepository extends JpaRepository<EspacioEstacionamiento, Integer> {
    List<EspacioEstacionamiento> findByTipoAndActivoTrue(TipoVehiculo tipo);
    List<EspacioEstacionamiento> findByActivoTrue();
}
