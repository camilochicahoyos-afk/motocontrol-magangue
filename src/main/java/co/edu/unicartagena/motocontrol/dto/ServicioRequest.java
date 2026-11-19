package co.edu.unicartagena.motocontrol.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ServicioRequest(
        @NotNull(message = "elija el mototaxista") Long conductorId,
        @NotNull(message = "elija la motocicleta") Long motocicletaId,
        @NotBlank(message = "es obligatorio") @Size(max = 80, message = "puede tener máximo 80 letras")
        String barrioOrigen,
        @NotBlank(message = "es obligatorio") @Size(max = 80, message = "puede tener máximo 80 letras")
        String barrioDestino,
        @NotNull(message = "es obligatoria") @Positive(message = "debe ser mayor que cero") BigDecimal tarifa) {
}
