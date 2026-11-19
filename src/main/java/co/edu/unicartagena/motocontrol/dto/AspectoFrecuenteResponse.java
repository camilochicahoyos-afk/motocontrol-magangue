package co.edu.unicartagena.motocontrol.dto;

/**
 * Opcion que los pasajeros marcan con frecuencia para un mototaxista.
 *
 * @param veces cuantas calificaciones la marcaron
 */
public record AspectoFrecuenteResponse(String codigo, String descripcion, boolean positivo, long veces) {
}
