package com.umg.parqueo.repository;

import com.umg.parqueo.entity.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Integer> {
    Optional<Semestre> findByVigenteTrue();
    Optional<Semestre> findByCodigo(String codigo);
}
