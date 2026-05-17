package com.umg.parqueo;

import com.umg.parqueo.entity.MarbeteDigital;
import com.umg.parqueo.enums.EstadoMarbete;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba unitaria del método isVigente() del marbete digital.
 * Cubre el requerimiento RF05 (validación de vigencia).
 */
class MarbeteDigitalTest {

    @Test
    void marbeteActivoDentroDeRangoEsVigente() {
        MarbeteDigital m = MarbeteDigital.builder()
                .estado(EstadoMarbete.ACTIVO)
                .fechaVigenciaInicio(LocalDate.now().minusDays(10))
                .fechaVigenciaFin(LocalDate.now().plusDays(10))
                .build();
        assertTrue(m.isVigente());
    }

    @Test
    void marbeteFueraDeRangoNoEsVigente() {
        MarbeteDigital m = MarbeteDigital.builder()
                .estado(EstadoMarbete.ACTIVO)
                .fechaVigenciaInicio(LocalDate.now().minusDays(30))
                .fechaVigenciaFin(LocalDate.now().minusDays(1))
                .build();
        assertFalse(m.isVigente());
    }

    @Test
    void marbeteRevocadoNoEsVigente() {
        MarbeteDigital m = MarbeteDigital.builder()
                .estado(EstadoMarbete.REVOCADO)
                .fechaVigenciaInicio(LocalDate.now().minusDays(10))
                .fechaVigenciaFin(LocalDate.now().plusDays(10))
                .build();
        assertFalse(m.isVigente());
    }
}
