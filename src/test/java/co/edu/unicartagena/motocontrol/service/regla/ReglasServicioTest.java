package co.edu.unicartagena.motocontrol.service.regla;

import static co.edu.unicartagena.motocontrol.DatosPrueba.HOY;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import co.edu.unicartagena.motocontrol.DatosPrueba;
import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoServicio;
import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.domain.repository.ServicioRepository;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

class ReglasServicioTest {

    private Conductor conductor;
    private Motocicleta moto;

    @BeforeEach
    void preparar() {
        conductor = DatosPrueba.conductor(1L);
        moto = DatosPrueba.motocicleta(10L, conductor);
    }

    private ContextoServicio contexto(long tarifa) {
        return new ContextoServicio(conductor, moto, BigDecimal.valueOf(tarifa), HOY);
    }

    @Test
    @DisplayName("RN-04: rechaza a un mototaxista suspendido")
    void conductorSuspendido() {
        conductor.cambiarEstado(EstadoConductor.SUSPENDIDO);

        assertThatThrownBy(() -> new ConductorActivoRegla().validar(contexto(3000)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("está suspendido");
    }

    @Test
    @DisplayName("RN-05: rechaza una licencia vencida")
    void licenciaVencida() {
        conductor.actualizarDatos("Luis", "Mercado", "3001234567", "Centro", "LIC-1", HOY.minusDays(1));

        assertThatThrownBy(() -> new LicenciaVigenteRegla().validar(contexto(3000)))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("RN-06: rechaza una moto que pertenece a otro mototaxista")
    void motoDeOtroConductor() {
        Motocicleta motoAjena = DatosPrueba.motocicleta(11L, DatosPrueba.conductor(2L));
        ContextoServicio ctx = new ContextoServicio(conductor, motoAjena, BigDecimal.valueOf(3000), HOY);

        assertThatThrownBy(() -> new MotocicletaAsignadaRegla().validar(ctx))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no está registrada a nombre de");
    }

    @Test
    @DisplayName("RN-07: rechaza una moto con SOAT vencido")
    void soatVencido() {
        moto.actualizarDocumentos(HOY.minusDays(1), HOY.plusMonths(3));

        assertThatThrownBy(() -> new DocumentosMotoVigentesRegla().validar(contexto(3000)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("El SOAT de la motocicleta ABC12D venció el 13/11/2026.");
    }

    @Test
    @DisplayName("RN-07: rechaza una moto con técnico-mecánica vencida")
    void tecnomecanicaVencida() {
        moto.actualizarDocumentos(HOY.plusMonths(3), HOY.minusDays(1));

        assertThatThrownBy(() -> new DocumentosMotoVigentesRegla().validar(contexto(3000)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("técnico-mecánica");
    }

    @Nested
    @DisplayName("RN-08: tarifa permitida entre 2000 y 15000")
    class Tarifa {

        private final TarifaPermitidaRegla regla =
                new TarifaPermitidaRegla(BigDecimal.valueOf(2000), BigDecimal.valueOf(15000));

        @Test
        void aceptaLimites() {
            assertThatCode(() -> regla.validar(contexto(2000))).doesNotThrowAnyException();
            assertThatCode(() -> regla.validar(contexto(15000))).doesNotThrowAnyException();
        }

        @Test
        void rechazaFueraDeRango() {
            assertThatThrownBy(() -> regla.validar(contexto(1500))).isInstanceOf(ReglaNegocioException.class);
            assertThatThrownBy(() -> regla.validar(contexto(20000)))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessage("La tarifa de $20.000 está fuera del rango permitido.")
                    .extracting("solucion").isEqualTo("Cobre entre $2.000 y $15.000 pesos.");
        }
    }

    @Test
    @DisplayName("RN-09: rechaza si el mototaxista ya tiene una carrera en curso")
    void servicioEnCurso() {
        ServicioRepository repo = mock(ServicioRepository.class);
        when(repo.existsByConductorIdAndEstado(1L, EstadoServicio.EN_CURSO)).thenReturn(true);

        assertThatThrownBy(() -> new UnServicioEnCursoRegla(repo).validar(contexto(3000)))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
