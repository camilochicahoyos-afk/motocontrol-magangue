package co.edu.unicartagena.motocontrol.service.impl;

import static co.edu.unicartagena.motocontrol.DatosPrueba.HOY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicartagena.motocontrol.DatosPrueba;
import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoServicio;
import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.domain.model.Servicio;
import co.edu.unicartagena.motocontrol.domain.repository.ConductorRepository;
import co.edu.unicartagena.motocontrol.domain.repository.MotocicletaRepository;
import co.edu.unicartagena.motocontrol.domain.repository.ServicioRepository;
import co.edu.unicartagena.motocontrol.dto.ResumenDiarioResponse;
import co.edu.unicartagena.motocontrol.dto.ServicioResponse;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import co.edu.unicartagena.motocontrol.mapper.ServicioMapper;
import co.edu.unicartagena.motocontrol.service.regla.ReglaServicio;

@ExtendWith(MockitoExtension.class)
class ServicioServiceImplTest {

    @Mock
    private ServicioRepository servicioRepository;
    @Mock
    private ConductorRepository conductorRepository;
    @Mock
    private MotocicletaRepository motocicletaRepository;

    private ReglaServicio regla;
    private ServicioServiceImpl service;
    private Conductor conductor;
    private Motocicleta moto;

    @BeforeEach
    void preparar() {
        regla = mock(ReglaServicio.class);
        service = new ServicioServiceImpl(servicioRepository, conductorRepository, motocicletaRepository,
                List.of(regla), new ServicioMapper(), DatosPrueba.RELOJ);
        conductor = DatosPrueba.conductor(1L);
        moto = DatosPrueba.motocicleta(10L, conductor);
    }

    @Test
    @DisplayName("Registra un servicio EN_CURSO cuando todas las reglas se cumplen")
    void registraServicio() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(motocicletaRepository.findById(10L)).thenReturn(Optional.of(moto));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));

        ServicioResponse respuesta = service.registrar(DatosPrueba.servicioRequest(3000));

        assertThat(respuesta.estado()).isEqualTo(EstadoServicio.EN_CURSO);
        assertThat(respuesta.placa()).isEqualTo("ABC12D");
        assertThat(respuesta.fechaHoraInicio()).isEqualTo(HOY.atTime(9, 30));
        verify(regla).validar(any());
    }

    @Test
    @DisplayName("Si una regla falla, el servicio no se guarda")
    void reglaFallidaNoGuarda() {
        when(conductorRepository.findById(1L)).thenReturn(Optional.of(conductor));
        when(motocicletaRepository.findById(10L)).thenReturn(Optional.of(moto));
        doThrow(new ReglaNegocioException("regla incumplida")).when(regla).validar(any());

        assertThatThrownBy(() -> service.registrar(DatosPrueba.servicioRequest(3000)))
                .isInstanceOf(ReglaNegocioException.class);
        verify(servicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("El resumen diario solo suma los ingresos de servicios finalizados")
    void resumenDiario() {
        Servicio finalizado = new Servicio(conductor, moto, "Centro", "Alfonso Lopez",
                BigDecimal.valueOf(3000), HOY.atTime(8, 0));
        finalizado.finalizar(HOY.atTime(8, 15));
        Servicio cancelado = new Servicio(conductor, moto, "Centro", "Yati",
                BigDecimal.valueOf(5000), HOY.atTime(9, 0));
        cancelado.cancelar(HOY.atTime(9, 5));
        Servicio enCurso = new Servicio(conductor, moto, "Centro", "Majagual",
                BigDecimal.valueOf(4000), HOY.atTime(9, 20));
        when(servicioRepository.findByFechaHoraInicioBetween(any(), any()))
                .thenReturn(List.of(finalizado, cancelado, enCurso));

        ResumenDiarioResponse resumen = service.resumenDiario(HOY);

        assertThat(resumen.totalServicios()).isEqualTo(3);
        assertThat(resumen.finalizados()).isEqualTo(1);
        assertThat(resumen.cancelados()).isEqualTo(1);
        assertThat(resumen.enCurso()).isEqualTo(1);
        assertThat(resumen.ingresosFinalizados()).isEqualByComparingTo("3000");
    }
}
