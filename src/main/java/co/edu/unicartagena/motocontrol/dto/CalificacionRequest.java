package co.edu.unicartagena.motocontrol.dto;

import java.util.Set;

import co.edu.unicartagena.motocontrol.domain.model.AspectoServicio;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Calificacion que el pasajero da al terminar una carrera.
 *
 * @param aspectos   opciones rapidas marcadas (opcional), por ejemplo AMABLE o MUY_LENTO
 * @param comentario opcional, maximo 300 caracteres
 */
public record CalificacionRequest(
        @NotNull(message = "indique la carrera que va a calificar")
        Long servicioId,

        @NotNull(message = "toque de 1 a 5 estrellas")
        @Min(value = 1, message = "debe ser de 1 a 5 estrellas")
        @Max(value = 5, message = "debe ser de 1 a 5 estrellas")
        Integer estrellas,

        Set<AspectoServicio> aspectos,

        @Size(max = 300, message = "puede tener máximo 300 letras")
        String comentario) {
}
