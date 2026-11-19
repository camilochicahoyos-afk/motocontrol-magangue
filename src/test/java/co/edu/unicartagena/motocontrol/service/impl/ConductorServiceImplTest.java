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
import co.edu.unicartagena.motocontrol.domain.repository.ConductorRepository;
import co.edu.unicartagena.motocontrol.dto.ConductorRequest;
import co.edu.unicartagena.motocontrol.dto.ConductorResponse;
import co.edu.unicartagena.motocontrol.exception.RecursoDuplicadoException;
import co.edu.unicartagena.motocontrol.exception.RecursoNoEncontradoException;
import co.edu.unicartagena.motocontrol.mapper.ConductorMapper;

@ExtendWith(MockitoExtension.class)
class ConductorServiceImplTest {

    @Mock
    private ConductorRepository conductorRepository;

    private ConductorServiceImpl service;

    @BeforeEach
    void preparar() {
        service = new ConductorServiceImpl(conductorRepository, new ConductorMapper(DatosPrueba.RELOJ));
    }

    @Test
    @DisplayName("Registra un conductor nuevo en estado ACTIVO")
    void registraConductor() {
        ConductorRequest request = DatosPrueba.conductorRequest();
        when(conductorRepository.existsByNumeroDocumento(request.numeroDocumento())).thenReturn(false);
        when(conductorRepository.existsByNumeroLicencia(request.numeroLicencia())).thenReturn(false);
        when(conductorRepository.save(any(Conductor.class))).thenAnswer(inv -> {
            Conductor c = inv.getArgument(0);
            c.asignarId(1L);
            return c;
        });

        ConductorResponse respuesta = service.registrar(request);

        assertThat(respuesta.id()).isEqualTo(1L);
        assertThat(respuesta.estado()).isEqualTo(EstadoConductor.ACTIVO);
        assertThat(respuesta.licenciaVigente()).isTrue();
    }

    @Test
    @DisplayName("RN-01: rechaza un documento ya registrado")
    void rechazaDocumentoDuplicado() {
        ConductorRequest request = DatosPrueba.conductorRequest();
        when(conductorRepository.existsByNumeroDocumento(request.numeroDocumento())).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(request))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining(request.numeroDocumento());
        verify(conductorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanza 404 cuando el conductor no existe")
    void conductorNoExiste() {
        when(conductorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("RN-03: eliminar un conductor lo deja INACTIVO (borrado logico)")
    void inactivarConductor() {
        Conductor conductor = DatosPrueba.conductor(1L);
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(conductorRepository.save(conductor)).thenReturn(conductor);

        service.inactivar(1L);

        assertThat(conductor.getEstado()).isEqualTo(EstadoConductor.INACTIVO);
        verify(conductorRepository).save(conductor);
    }
}
