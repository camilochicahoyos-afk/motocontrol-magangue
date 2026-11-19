package co.edu.unicartagena.motocontrol.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;

/**
 * Reputacion de un mototaxista segun lo que opinan los pasajeros.
 *
 * @param puesto              lugar en el ranking (null si aun no tiene calificaciones)
 * @param promedio            promedio de estrellas con un decimal (null si no tiene calificaciones)
 * @param paraRevision        RN-13: 3 o mas calificaciones y promedio menor a 3
 * @param distribucion        cuantas calificaciones hay de 5, 4, 3, 2 y 1 estrellas
 * @param aspectosFrecuentes  las tres opciones mas marcadas
 */
public record ReputacionResponse(
        Integer puesto,
        Long conductorId,
        String conductorNombre,
        EstadoConductor estadoConductor,
        long totalCalificaciones,
        BigDecimal promedio,
        boolean paraRevision,
        Map<Integer, Long> distribucion,
        List<AspectoFrecuenteResponse> aspectosFrecuentes) {
}
