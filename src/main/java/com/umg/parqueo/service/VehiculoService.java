package com.umg.parqueo.service;

import com.umg.parqueo.dto.request.VehiculoRequest;
import com.umg.parqueo.dto.response.VehiculoResponse;
import com.umg.parqueo.entity.Estudiante;
import com.umg.parqueo.entity.Vehiculo;
import com.umg.parqueo.exception.RecursoNoEncontradoException;
import com.umg.parqueo.exception.ReglaNegocioException;
import com.umg.parqueo.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final EstudianteService estudianteService;

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarPorEstudiante(Long estudianteId) {
        return vehiculoRepository.findByEstudiante_IdAndActivoTrue(estudianteId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public VehiculoResponse registrar(Long estudianteId, VehiculoRequest req) {
        if (vehiculoRepository.existsByPlaca(req.getPlaca())) {
            throw new ReglaNegocioException("Ya existe un vehículo con la placa " + req.getPlaca());
        }
        Estudiante est = estudianteService.obtenerPorId(estudianteId);

        Vehiculo v = Vehiculo.builder()
                .estudiante(est)
                .placa(req.getPlaca().toUpperCase())
                .tipo(req.getTipo())
                .marca(req.getMarca())
                .modelo(req.getModelo())
                .color(req.getColor())
                .anio(req.getAnio())
                .activo(true)
                .build();
        return toResponse(vehiculoRepository.save(v));
    }

    public Vehiculo obtener(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Vehículo con id " + id + " no encontrado"));
    }

    private VehiculoResponse toResponse(Vehiculo v) {
        return VehiculoResponse.builder()
                .id(v.getId())
                .placa(v.getPlaca())
                .tipo(v.getTipo())
                .marca(v.getMarca())
                .modelo(v.getModelo())
                .color(v.getColor())
                .anio(v.getAnio())
                .activo(v.getActivo())
                .build();
    }
}
