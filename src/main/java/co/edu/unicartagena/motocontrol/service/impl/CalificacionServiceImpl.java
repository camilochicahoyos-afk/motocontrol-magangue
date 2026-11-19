package co.edu.unicartagena.motocontrol.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicartagena.motocontrol.domain.model.AspectoServicio;
import co.edu.unicartagena.motocontrol.domain.model.Calificacion;
import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.domain.model.Servicio;
import co.edu.unicartagena.motocontrol.domain.repository.CalificacionRepository;
import co.edu.unicartagena.motocontrol.domain.repository.ConductorRepository;
import co.edu.unicartagena.motocontrol.domain.repository.ServicioRepository;
import co.edu.unicartagena.motocontrol.dto.AspectoFrecuenteResponse;
import co.edu.unicartagena.motocontrol.dto.CalificacionRequest;
import co.edu.unicartagena.motocontrol.dto.CalificacionResponse;
import co.edu.unicartagena.motocontrol.dto.OpcionesCalificacionResponse;
import co.edu.unicartagena.motocontrol.dto.OpinionesConductorResponse;
import co.edu.unicartagena.motocontrol.dto.ReputacionResponse;
import co.edu.unicartagena.motocontrol.exception.RecursoDuplicadoException;
import co.edu.unicartagena.motocontrol.exception.RecursoNoEncontradoException;
import co.edu.unicartagena.motocontrol.mapper.CalificacionMapper;
import co.edu.unicartagena.motocontrol.service.CalificacionService;

@Service
@Transactional
public class CalificacionServiceImpl implements CalificacionService {

    /** RN-13: con este numero de calificaciones o mas... */
    static final int MINIMO_CALIFICACIONES_REVISION = 3;
    /** ...y un promedio menor a este, el mototaxista queda "para revision". */
    static final BigDecimal PROMEDIO_MINIMO = BigDecimal.valueOf(3);

    private static final int ASPECTOS_FRECUENTES = 3;

    private final CalificacionRepository calificacionRepository;
    private final ServicioRepository servicioRepository;
    private final ConductorRepository conductorRepository;
    private final CalificacionMapper mapper;
    private final Clock clock;

    public CalificacionServiceImpl(CalificacionRepository calificacionRepository,
                                   ServicioRepository servicioRepository,
                                   ConductorRepository conductorRepository,
                                   CalificacionMapper mapper,
                                   Clock clock) {
        this.calificacionRepository = calificacionRepository;
        this.servicioRepository = servicioRepository;
        this.conductorRepository = conductorRepository;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Override
    public CalificacionResponse calificar(CalificacionRequest request) {
        Servicio servicio = servicioRepository.findById(request.servicioId())
                .orElseThrow(() -> RecursoNoEncontradoException.carrera(request.servicioId()));
        if (calificacionRepository.existsByServicioId(servicio.getId())) {
            throw new RecursoDuplicadoException("Esta carrera ya fue calificada.",
                    "Cada carrera se califica una sola vez.");
        }
        Calificacion calificacion = new Calificacion(servicio, request.estrellas(), request.aspectos(),
                request.comentario(), LocalDateTime.now(clock));
        return mapper.aRespuesta(calificacionRepository.save(calificacion));
    }

    @Override
    @Transactional(readOnly = true)
    public OpcionesCalificacionResponse opciones() {
        return new OpcionesCalificacionResponse(Calificacion.escala(),
                Arrays.stream(AspectoServicio.values()).filter(AspectoServicio::isPositivo)
                        .map(CalificacionMapper::aOpcion).toList(),
                Arrays.stream(AspectoServicio.values()).filter(aspecto -> !aspecto.isPositivo())
                        .map(CalificacionMapper::aOpcion).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OpinionesConductorResponse reputacion(Long conductorId) {
        Conductor conductor = conductorRepository.findById(conductorId)
                .orElseThrow(() -> RecursoNoEncontradoException.mototaxista(conductorId));
        List<Calificacion> calificaciones = calificacionRepository.findByConductorIdOrderByFechaDesc(conductorId);
        Integer puesto = ranking().stream()
                .filter(item -> item.conductorId().equals(conductorId))
                .map(ReputacionResponse::puesto)
                .findFirst()
                .orElse(null);
        return new OpinionesConductorResponse(calcular(conductor, calificaciones, puesto),
                calificaciones.stream().map(mapper::aRespuesta).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReputacionResponse> ranking() {
        Map<Long, List<Calificacion>> porConductor = calificacionRepository.findAll().stream()
                .collect(Collectors.groupingBy(c -> c.getConductor().getId()));

        List<ReputacionResponse> sinPuesto = conductorRepository.findAll().stream()
                .filter(conductor -> conductor.getEstado() != EstadoConductor.INACTIVO)
                .map(conductor -> calcular(conductor, porConductor.getOrDefault(conductor.getId(), List.of()), null))
                .sorted(Comparator
                        .comparing(ReputacionResponse::promedio, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ReputacionResponse::totalCalificaciones, Comparator.reverseOrder())
                        .thenComparing(ReputacionResponse::conductorNombre))
                .toList();

        List<ReputacionResponse> ranking = new ArrayList<>();
        int puesto = 1;
        for (ReputacionResponse item : sinPuesto) {
            Integer lugar = item.totalCalificaciones() > 0 ? puesto++ : null;
            ranking.add(new ReputacionResponse(lugar, item.conductorId(), item.conductorNombre(),
                    item.estadoConductor(), item.totalCalificaciones(), item.promedio(), item.paraRevision(),
                    item.distribucion(), item.aspectosFrecuentes()));
        }
        return ranking;
    }

    /** Promedio con un decimal, cuantas hay de cada estrella, lo mas marcado y la marca de RN-13. */
    static ReputacionResponse calcular(Conductor conductor, List<Calificacion> calificaciones, Integer puesto) {
        long total = calificaciones.size();
        BigDecimal promedio = total == 0 ? null
                : BigDecimal.valueOf(calificaciones.stream().mapToInt(Calificacion::getEstrellas).sum())
                        .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
        boolean paraRevision = total >= MINIMO_CALIFICACIONES_REVISION
                && promedio.compareTo(PROMEDIO_MINIMO) < 0;

        Map<Integer, Long> distribucion = new LinkedHashMap<>();
        for (int estrellas = 5; estrellas >= 1; estrellas--) {
            int buscadas = estrellas;
            distribucion.put(estrellas, calificaciones.stream().filter(c -> c.getEstrellas() == buscadas).count());
        }

        Map<AspectoServicio, Long> conteo = new EnumMap<>(AspectoServicio.class);
        calificaciones.forEach(c -> c.getAspectos().forEach(a -> conteo.merge(a, 1L, Long::sum)));
        List<AspectoFrecuenteResponse> frecuentes = conteo.entrySet().stream()
                .sorted(Map.Entry.<AspectoServicio, Long>comparingByValue().reversed()
                        .thenComparing(entrada -> entrada.getKey().ordinal()))
                .limit(ASPECTOS_FRECUENTES)
                .map(entrada -> new AspectoFrecuenteResponse(entrada.getKey().name(),
                        entrada.getKey().getDescripcion(), entrada.getKey().isPositivo(), entrada.getValue()))
                .toList();

        return new ReputacionResponse(puesto, conductor.getId(), conductor.nombreCompleto(), conductor.getEstado(),
                total, promedio, paraRevision, distribucion, frecuentes);
    }
}
