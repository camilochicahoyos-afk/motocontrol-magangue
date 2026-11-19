package co.edu.unicartagena.motocontrol.dto;

import java.util.List;

/**
 * Reputacion de un mototaxista junto con todas sus opiniones, de la mas reciente a la mas antigua.
 */
public record OpinionesConductorResponse(ReputacionResponse reputacion, List<CalificacionResponse> opiniones) {
}
