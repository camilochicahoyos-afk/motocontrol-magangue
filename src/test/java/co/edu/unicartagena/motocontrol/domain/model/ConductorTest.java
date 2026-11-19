package co.edu.unicartagena.motocontrol.domain.model;

import static co.edu.unicartagena.motocontrol.DatosPrueba.HOY;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.unicartagena.motocontrol.DatosPrueba;

class ConductorTest {

    @Test
    @DisplayName("Un conductor recien creado queda ACTIVO")
    void conductorNuevoActivo() {
        assertThat(DatosPrueba.conductor(1L).estaActivo()).isTrue();
    }

    @Test
    @DisplayName("La licencia es vigente hasta el mismo dia de su vencimiento")
    void licenciaVigenteHastaElDiaDeVencimiento() {
        Conductor conductor = DatosPrueba.conductor(1L);
        conductor.actualizarDatos("Luis", "Mercado", "3001234567", "Centro", "LIC-1", HOY);

        assertThat(conductor.tieneLicenciaVigente(HOY)).isTrue();
        assertThat(conductor.tieneLicenciaVigente(HOY.plusDays(1))).isFalse();
    }

    @Test
    @DisplayName("Un conductor suspendido deja de estar activo")
    void conductorSuspendido() {
        Conductor conductor = DatosPrueba.conductor(1L);
        conductor.cambiarEstado(EstadoConductor.SUSPENDIDO);

        assertThat(conductor.estaActivo()).isFalse();
    }
}
