package co.edu.unicartagena.motocontrol.domain.model;

import static co.edu.unicartagena.motocontrol.DatosPrueba.HOY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.unicartagena.motocontrol.DatosPrueba;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

class ServicioTest {

    private Servicio servicio;
    private final LocalDateTime inicio = HOY.atTime(10, 0);

    @BeforeEach
    void preparar() {
        Conductor conductor = DatosPrueba.conductor(1L);
        servicio = new Servicio(conductor, DatosPrueba.motocicleta(10L, conductor),
                "Centro", "Pueblo Nuevo", BigDecimal.valueOf(3000), inicio);
    }

    @Test
    @DisplayName("Una carrera nueva inicia en estado EN_CURSO")
    void servicioNuevoEnCurso() {
        assertThat(servicio.getEstado()).isEqualTo(EstadoServicio.EN_CURSO);
        assertThat(servicio.getFechaHoraFin()).isNull();
    }

    @Test
    @DisplayName("Finalizar registra la hora de fin y cambia el estado")
    void finalizarServicio() {
        servicio.finalizar(inicio.plusMinutes(12));

        assertThat(servicio.getEstado()).isEqualTo(EstadoServicio.FINALIZADO);
        assertThat(servicio.getFechaHoraFin()).isEqualTo(inicio.plusMinutes(12));
    }

    @Test
    @DisplayName("RN-10: no se puede cancelar una carrera ya finalizada")
    void noCancelaFinalizado() {
        servicio.finalizar(inicio.plusMinutes(12));

        assertThatThrownBy(() -> servicio.cancelar(inicio.plusMinutes(15)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya está finalizada");
    }
}
