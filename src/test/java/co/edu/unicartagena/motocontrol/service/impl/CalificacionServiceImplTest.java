package co.edu.unicartagena.motocontrol.service.impl;

import static co.edu.unicartagena.motocontrol.DatosPrueba.HOY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicartagena.motocontrol.DatosPrueba;
import co.edu.unicartagena.motocontrol.domain.model.AspectoServicio;
import co.edu.unicartagena.motocontrol.domain.model.Calificacion;
import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.repository.CalificacionRepository;
import co.edu.unicartagena.motocontrol.domain.repository.ConductorRepository;
import co.edu.unicartagena.motocontrol.domain.repository.ServicioRepository;
import co.edu.unicartagena.motocontrol.dto.CalificacionRequest;
import co.edu.unicartagena.motocontrol.dto.CalificacionResponse;
import co.edu.unicartagena.motocontrol.dto.ReputacionResponse;
import co.edu.unicartagena.motocontrol.exception.RecursoDuplicadoException;
import co.edu.unicartagena.motocontrol.mapper.CalificacionMapper;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceImplTest {

    @Mock
    private CalificacionRepository calificacionRepository;
    @Mock
    private ServicioRepository servicioRepository;
    @Mock
    private ConductorRepository conductorRepository;

    private CalificacionServiceImpl service;
    private Conductor conductor;
    private long siguienteCarrera = 100;

    @BeforeEach
    void preparar() {
        service = new CalificacionServiceImpl(calificacionRepository, servicioRepository, conductorRepository,
                new CalificacionMapper(), DatosPrueba.RELOJ);
        conductor = DatosPrueba.conductor(1L);
    }

    /** Una calificacion de una carrera distinta del mismo mototaxista. */
    private Calificacion calificacion(int estrellas, AspectoServicio... aspectos) {
        return new Calificacion(DatosPrueba.carreraFinalizada(siguienteCarrera++, conductor), estrellas,
                Set.of(aspectos), null, HOY.atTime(10, 0));
    }

    private ReputacionResponse rankingCon(int... estrellas) {
        List<Calificacion> calificaciones = new ArrayList<>();
        for (int valor : estrellas) {
            calificaciones.add(calificacion(valor));
        }
        when(conductorRepository.findAll()).thenReturn(List.of(conductor));
        when(calificacionRepository.findAll()).thenReturn(calificaciones);
        return service.ranking().get(0);
    }

    @Test
    @DisplayName("Guarda la calificación de una carrera finalizada con sus opciones")
    void calificaCarrera() {
        when(servicioRepository.findById(5L)).thenReturn(Optional.of(DatosPrueba.carreraFinalizada(5L, conductor)));
        when(calificacionRepository.existsByServicioId(5L)).thenReturn(false);
        when(calificacionRepository.save(any(Calificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        CalificacionResponse respuesta = service.calificar(new CalificacionRequest(5L, 5,
                Set.of(AspectoServicio.CONDUCE_CON_PRECAUCION), "Muy buen servicio"));

        assertThat(respuesta.estrellas()).isEqualTo(5);
        assertThat(respuesta.valoracion()).isEqualTo("Excelente");
        assertThat(respuesta.conductorId()).isEqualTo(1L);
        assertThat(respuesta.recorrido()).isEqualTo("Centro → Pueblo Nuevo");
        assertThat(respuesta.aspectos()).extracting("descripcion").containsExactly("Conduce con precaución");
        assertThat(respuesta.fecha()).isEqualTo(HOY.atTime(9, 30));
    }

    @Test
    @DisplayName("RN-12: no deja calificar dos veces la misma carrera")
    void rechazaCalificacionRepetida() {
        when(servicioRepository.findById(5L)).thenReturn(Optional.of(DatosPrueba.carreraFinalizada(5L, conductor)));
        when(calificacionRepository.existsByServicioId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.calificar(new CalificacionRequest(5L, 4, Set.of(), null)))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessage("Esta carrera ya fue calificada.");
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Calcula el promedio con un decimal, las estrellas y lo que más marcan los pasajeros")
    void calculaPromedioYDistribucion() {
        List<Calificacion> calificaciones = List.of(
                calificacion(5, AspectoServicio.AMABLE, AspectoServicio.PUNTUAL),
                calificacion(4, AspectoServicio.AMABLE),
                calificacion(4, AspectoServicio.AMABLE, AspectoServicio.MUY_LENTO));
        when(conductorRepository.findAll()).thenReturn(List.of(conductor));
        when(calificacionRepository.findAll()).thenReturn(calificaciones);

        ReputacionResponse reputacion = service.ranking().get(0);

        assertThat(reputacion.puesto()).isEqualTo(1);
        assertThat(reputacion.promedio()).isEqualByComparingTo("4.3");
        assertThat(reputacion.distribucion()).containsEntry(5, 1L).containsEntry(4, 2L).containsEntry(1, 0L);
        assertThat(reputacion.aspectosFrecuentes().get(0).descripcion()).isEqualTo("Amable y respetuoso");
        assertThat(reputacion.aspectosFrecuentes().get(0).veces()).isEqualTo(3);
        assertThat(reputacion.paraRevision()).isFalse();
    }

    @Test
    @DisplayName("RN-13: con 3 o más calificaciones y promedio menor a 3 queda para revisión")
    void marcaParaRevision() {
        ReputacionResponse reputacion = rankingCon(2, 3, 2);

        assertThat(reputacion.promedio()).isEqualByComparingTo("2.3");
        assertThat(reputacion.paraRevision()).isTrue();
    }

    @Test
    @DisplayName("RN-13: con menos de 3 calificaciones no se marca, aunque el promedio sea bajo")
    void noMarcaConPocasCalificaciones() {
        ReputacionResponse reputacion = rankingCon(1, 2);

        assertThat(reputacion.promedio()).isEqualByComparingTo("1.5");
        assertThat(reputacion.paraRevision()).isFalse();
    }
}
