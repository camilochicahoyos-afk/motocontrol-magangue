package co.edu.unicartagena.motocontrol.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato JSON uniforme para todas las respuestas de error de la API.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        List<String> detalles) {
}
