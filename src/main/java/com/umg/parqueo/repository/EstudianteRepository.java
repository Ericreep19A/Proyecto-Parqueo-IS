package com.umg.parqueo.repository;

import com.umg.parqueo.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByUsuario_Id(Long usuarioId);
    Optional<Estudiante> findByUsuario_Correo(String correo);
    Optional<Estudiante> findByCarne(String carne);
}
