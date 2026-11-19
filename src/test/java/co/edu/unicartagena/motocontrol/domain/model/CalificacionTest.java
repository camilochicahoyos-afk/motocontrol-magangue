package co.edu.unicartagena.motocontrol.domain.model;

import static co.edu.unicartagena.motocontrol.DatosPrueba.HOY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.unicartagena.motocontrol.DatosPrueba;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

class CalificacionTest {

    private final Conductor conductor = DatosPrueba.conductor(1L);
    private final LocalDateTime ahora = HOY.atTime(9, 30);

    private Servicio carreraEnCurso() {
        return new Servicio(conductor, DatosPrueba.motocicleta(10L, conductor), "Centro", "Yatí",
                BigDecimal.valueOf(3000), HOY.atTime(9, 0));
    }

    @Test
    @DisplayName("RN-11: una carrera finalizada se califica y queda a nombre de su mototaxista")
    void calificaCarreraFinalizada() {
        Servicio carrera = DatosPrueba.carreraFinalizada(5L, conductor);

        Calificacion calificacion = new Calificacion(carrera, 4,
                Set.of(AspectoServicio.AMABLE, AspectoServicio.MUY_LENTO), "  Buen trato, pero lento  ", ahora);

        assertThat(calificacion.getConductor()).isSameAs(conductor);
        assertThat(calificacion.getEstrellas()).isEqualTo(4);
        assertThat(Calificacion.valoracion(calificacion.getEstrellas())).isEqualTo("Bueno");
        assertThat(calificacion.getAspectos()).containsExactlyInAnyOrder(AspectoServicio.AMABLE,
                AspectoServicio.MUY_LENTO);
        assertThat(calificacion.getComentario()).isEqualTo("Buen trato, pero lento");
    }

    @Test
    @DisplayName("RN-11: no se puede calificar una carrera que sigue en curso")
    void rechazaCarreraEnCurso() {
        Servicio carrera = carreraEnCurso();

        assertThatThrownBy(() -> new Calificacion(carrera, 5, Set.of(), null, ahora))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("está en curso");
    }

    @Test
    @DisplayName("RN-11: no se puede calificar una carrera cancelada")
    void rechazaCarreraCancelada() {
        Servicio carrera = carreraEnCurso();
        carrera.cancelar(HOY.atTime(9, 5));

        assertThatThrownBy(() -> new Calificacion(carrera, 3, Set.of(), null, ahora))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("está cancelada");
    }
}
