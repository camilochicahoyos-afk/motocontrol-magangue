package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CalificacionResponse(
        Long id,
        Long servicioId,
        String recorrido,
        Long conductorId,
        String conductorNombre,
        int estrellas,
        String valoracion,
        List<OpcionCalificacionResponse> aspectos,
        String comentario,
        LocalDateTime fecha) {
}
