package co.edu.unicartagena.motocontrol.dto;

import java.util.List;

/**
 * Lo que necesita la pantalla de calificar: la palabra de cada estrella y las opciones rapidas.
 *
 * @param escala          posicion 0 = 1 estrella ("Muy malo") ... posicion 4 = 5 estrellas ("Excelente")
 * @param loBueno         opciones positivas
 * @param loQueDebeMejorar opciones por mejorar
 */
public record OpcionesCalificacionResponse(
        List<String> escala,
        List<OpcionCalificacionResponse> loBueno,
        List<OpcionCalificacionResponse> loQueDebeMejorar) {
}
