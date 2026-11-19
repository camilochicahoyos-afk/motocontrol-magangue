package co.edu.unicartagena.motocontrol.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.edu.unicartagena.motocontrol.domain.model.EstadoServicio;

public record ServicioResponse(
        Long id,
        Long conductorId,
        String conductorNombre,
        Long motocicletaId,
        String placa,
        String barrioOrigen,
        String barrioDestino,
        BigDecimal tarifa,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        EstadoServicio estado) {
}
