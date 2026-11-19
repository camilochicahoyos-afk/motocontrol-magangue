package co.edu.unicartagena.motocontrol.service.impl;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoServicio;
import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.domain.model.Servicio;
import co.edu.unicartagena.motocontrol.domain.repository.ConductorRepository;
import co.edu.unicartagena.motocontrol.domain.repository.MotocicletaRepository;
import co.edu.unicartagena.motocontrol.domain.repository.ServicioRepository;
import co.edu.unicartagena.motocontrol.dto.ResumenDiarioResponse;
import co.edu.unicartagena.motocontrol.dto.ServicioRequest;
import co.edu.unicartagena.motocontrol.dto.ServicioResponse;
import co.edu.unicartagena.motocontrol.exception.RecursoNoEncontradoException;
import co.edu.unicartagena.motocontrol.mapper.ServicioMapper;
import co.edu.unicartagena.motocontrol.service.ServicioService;
import co.edu.unicartagena.motocontrol.service.regla.ContextoServicio;
import co.edu.unicartagena.motocontrol.service.regla.ReglaServicio;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final ConductorRepository conductorRepository;
    private final MotocicletaRepository motocicletaRepository;
    private final List<ReglaServicio> reglas;
    private final ServicioMapper mapper;
    private final Clock clock;

    public ServicioServiceImpl(ServicioRepository servicioRepository,
                               ConductorRepository conductorRepository,
                               MotocicletaRepository motocicletaRepository,
                               List<ReglaServicio> reglas,
                               ServicioMapper mapper,
                               Clock clock) {
        this.servicioRepository = servicioRepository;
        this.conductorRepository = conductorRepository;
        this.motocicletaRepository = motocicletaRepository;
        this.reglas = reglas;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Override
    public ServicioResponse registrar(ServicioRequest request) {
        Conductor conductor = conductorRepository.findById(request.conductorId())
                .orElseThrow(() -> RecursoNoEncontradoException.mototaxista(request.conductorId()));
        Motocicleta motocicleta = motocicletaRepository.findById(request.motocicletaId())
                .orElseThrow(() -> RecursoNoEncontradoException.motocicleta(request.motocicletaId()));

        ContextoServicio contexto = new ContextoServicio(conductor, motocicleta, request.tarifa(), LocalDate.now(clock));
        reglas.forEach(regla -> regla.validar(contexto));

        Servicio servicio = new Servicio(conductor, motocicleta, request.barrioOrigen().trim(),
                request.barrioDestino().trim(), request.tarifa(), LocalDateTime.now(clock));
        return mapper.aRespuesta(servicioRepository.save(servicio));
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponse buscarPorId(Long id) {
        return mapper.aRespuesta(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponse> listarPorConductor(Long conductorId) {
        if (!conductorRepository.existsById(conductorId)) {
            throw RecursoNoEncontradoException.mototaxista(conductorId);
        }
        return servicioRepository.findByConductorId(conductorId).stream().map(mapper::aRespuesta).toList();
    }

    @Override
    public ServicioResponse finalizar(Long id) {
        Servicio servicio = obtener(id);
        servicio.finalizar(LocalDateTime.now(clock));
        return mapper.aRespuesta(servicioRepository.save(servicio));
    }

    @Override
    public ServicioResponse cancelar(Long id) {
        Servicio servicio = obtener(id);
        servicio.cancelar(LocalDateTime.now(clock));
        return mapper.aRespuesta(servicioRepository.save(servicio));
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenDiarioResponse resumenDiario(LocalDate fecha) {
        List<Servicio> servicios = servicioRepository.findByFechaHoraInicioBetween(
                fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay().minusNanos(1));

        long finalizados = contarPorEstado(servicios, EstadoServicio.FINALIZADO);
        long cancelados = contarPorEstado(servicios, EstadoServicio.CANCELADO);
        long enCurso = contarPorEstado(servicios, EstadoServicio.EN_CURSO);
        BigDecimal ingresos = servicios.stream()
                .filter(s -> s.getEstado() == EstadoServicio.FINALIZADO)
                .map(Servicio::getTarifa)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumenDiarioResponse(fecha, servicios.size(), finalizados, cancelados, enCurso, ingresos);
    }

    private static long contarPorEstado(List<Servicio> servicios, EstadoServicio estado) {
        return servicios.stream().filter(s -> s.getEstado() == estado).count();
    }

    private Servicio obtener(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.carrera(id));
    }
}
