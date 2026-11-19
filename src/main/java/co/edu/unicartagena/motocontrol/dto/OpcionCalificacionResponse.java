package co.edu.unicartagena.motocontrol.dto;

/**
 * Una opcion rapida de calificacion.
 *
 * @param codigo      valor que se envia al calificar (por ejemplo AMABLE)
 * @param descripcion texto que ve el pasajero (por ejemplo "Amable y respetuoso")
 * @param positivo    true si es algo bueno; false si es algo por mejorar
 */
public record OpcionCalificacionResponse(String codigo, String descripcion, boolean positivo) {
}
