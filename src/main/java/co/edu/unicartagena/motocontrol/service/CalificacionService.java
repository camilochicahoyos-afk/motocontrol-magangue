package co.edu.unicartagena.motocontrol.service;

import java.util.List;

import co.edu.unicartagena.motocontrol.dto.CalificacionRequest;
import co.edu.unicartagena.motocontrol.dto.CalificacionResponse;
import co.edu.unicartagena.motocontrol.dto.OpcionesCalificacionResponse;
import co.edu.unicartagena.motocontrol.dto.OpinionesConductorResponse;
import co.edu.unicartagena.motocontrol.dto.ReputacionResponse;

/**
 * Calificacion de los mototaxistas por parte de los pasajeros.
 */
public interface CalificacionService {

    /** RN-11 y RN-12: solo carreras finalizadas y una sola calificacion por carrera. */
    CalificacionResponse calificar(CalificacionRequest request);

    OpcionesCalificacionResponse opciones();

    /** Reputacion de un mototaxista con todas sus opiniones. */
    OpinionesConductorResponse reputacion(Long conductorId);

    /** Mototaxistas ordenados por promedio; RN-13 marca "para revision" a los de promedio bajo. */
    List<ReputacionResponse> ranking();
}
