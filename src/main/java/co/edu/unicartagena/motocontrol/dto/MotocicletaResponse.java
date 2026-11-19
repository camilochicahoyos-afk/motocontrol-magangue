package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDate;

public record MotocicletaResponse(
        Long id,
        String placa,
        String marca,
        String linea,
        Integer modelo,
        Integer cilindraje,
        LocalDate vencimientoSoat,
        LocalDate vencimientoTecnomecanica,
        boolean documentosVigentes,
        boolean activa,
        Long conductorId,
        String conductorNombre) {
}
