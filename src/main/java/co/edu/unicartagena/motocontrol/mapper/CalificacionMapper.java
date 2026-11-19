package co.edu.unicartagena.motocontrol.mapper;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.AspectoServicio;
import co.edu.unicartagena.motocontrol.domain.model.Calificacion;
import co.edu.unicartagena.motocontrol.dto.CalificacionResponse;
import co.edu.unicartagena.motocontrol.dto.OpcionCalificacionResponse;

@Component
public class CalificacionMapper {

    public CalificacionResponse aRespuesta(Calificacion calificacion) {
        List<OpcionCalificacionResponse> aspectos = calificacion.getAspectos().stream()
                .sorted(Comparator.comparingInt(AspectoServicio::ordinal))
                .map(CalificacionMapper::aOpcion)
                .toList();
        return new CalificacionResponse(calificacion.getId(), calificacion.getServicio().getId(),
                calificacion.getServicio().recorrido(), calificacion.getConductor().getId(),
                calificacion.getConductor().nombreCompleto(), calificacion.getEstrellas(),
                Calificacion.valoracion(calificacion.getEstrellas()), aspectos, calificacion.getComentario(),
                calificacion.getFecha());
    }

    public static OpcionCalificacionResponse aOpcion(AspectoServicio aspecto) {
        return new OpcionCalificacionResponse(aspecto.name(), aspecto.getDescripcion(), aspecto.isPositivo());
    }
}
