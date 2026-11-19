package co.edu.unicartagena.motocontrol.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicartagena.motocontrol.DatosPrueba;
import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.domain.repository.ConductorRepository;
import co.edu.unicartagena.motocontrol.domain.repository.MotocicletaRepository;
import co.edu.unicartagena.motocontrol.dto.MotocicletaResponse;
import co.edu.unicartagena.motocontrol.exception.RecursoDuplicadoException;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import co.edu.unicartagena.motocontrol.mapper.MotocicletaMapper;

@ExtendWith(MockitoExtension.class)
class MotocicletaServiceImplTest {

    @Mock
    private MotocicletaRepository motocicletaRepository;
    @Mock
    private ConductorRepository conductorRepository;

    private MotocicletaServiceImpl service;
    private Conductor conductor;

    @BeforeEach
    void preparar() {
        service = new MotocicletaServiceImpl(motocicletaRepository, conductorRepository,
                new MotocicletaMapper(DatosPrueba.RELOJ));
        conductor = DatosPrueba.conductor(1L);
    }

    @Test
    @DisplayName("Registra la moto y normaliza la placa a mayusculas")
    void registraMotocicleta() {
        when(motocicletaRepository.existsByPlaca("ABC12D")).thenReturn(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(motocicletaRepository.countByConductorIdAndActivaTrue(1L)).thenReturn(0L);
        when(motocicletaRepository.save(any(Motocicleta.class))).thenAnswer(inv -> {
            Motocicleta m = inv.getArgument(0);
            m.asignarId(10L);
            return m;
        });

        MotocicletaResponse respuesta = service.registrar(DatosPrueba.motocicletaRequest(1L));

        assertThat(respuesta.placa()).isEqualTo("ABC12D");
        assertThat(respuesta.documentosVigentes()).isTrue();
        assertThat(respuesta.conductorId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("RN-01: rechaza una placa ya registrada")
    void rechazaPlacaDuplicada() {
        when(motocicletaRepository.existsByPlaca("ABC12D")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(DatosPrueba.motocicletaRequest(1L)))
                .isInstanceOf(RecursoDuplicadoException.class);
        verify(motocicletaRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-02: un conductor no puede tener mas de dos motos activas")
    void rechazaTerceraMoto() {
        when(motocicletaRepository.existsByPlaca("ABC12D")).thenReturn(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(motocicletaRepository.countByConductorIdAndActivaTrue(1L))
                .thenReturn((long) MotocicletaServiceImpl.MAXIMO_MOTOS_ACTIVAS);

        assertThatThrownBy(() -> service.registrar(DatosPrueba.motocicletaRequest(1L)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("motocicletas activas");
    }

    @Test
    @DisplayName("No registra motos a un conductor INACTIVO")
    void rechazaConductorInactivo() {
        conductor.cambiarEstado(EstadoConductor.INACTIVO);
        when(motocicletaRepository.existsByPlaca("ABC12D")).thenReturn(false);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));

        assertThatThrownBy(() -> service.registrar(DatosPrueba.motocicletaRequest(1L)))
                .isInstanceOf(ReglaNegocioException.class);
        verify(motocicletaRepository, never()).save(any());
    }
}
