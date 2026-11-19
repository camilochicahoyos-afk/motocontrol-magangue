package co.edu.unicartagena.motocontrol.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ServicioRequest(
        @NotNull Long conductorId,
        @NotNull Long motocicletaId,
        @NotBlank @Size(max = 80) String barrioOrigen,
        @NotBlank @Size(max = 80) String barrioDestino,
        @NotNull @Positive BigDecimal tarifa) {
}
