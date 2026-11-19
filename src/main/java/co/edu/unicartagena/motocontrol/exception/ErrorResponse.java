package co.edu.unicartagena.motocontrol.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato JSON uniforme para todas las respuestas de error de la API.
 *
 * @param mensaje  que paso, en palabras sencillas
 * @param solucion como corregirlo
 * @param detalles un renglon por cada campo mal diligenciado (solo en errores 400)
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String solucion,
        String ruta,
        List<String> detalles) {
}
