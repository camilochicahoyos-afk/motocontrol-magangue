package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MotocicletaRequest(
        @NotBlank(message = "es obligatoria")
        @Pattern(regexp = "[A-Za-z]{3}\\d{2}[A-Za-z]",
                message = "debe tener 3 letras, 2 números y 1 letra, por ejemplo ABC12D")
        String placa,

        @NotBlank(message = "es obligatoria") @Size(max = 40, message = "puede tener máximo 40 letras")
        String marca,

        @NotBlank(message = "es obligatoria") @Size(max = 40, message = "puede tener máximo 40 letras")
        String linea,

        @NotNull(message = "es obligatorio")
        @Min(value = 1990, message = "debe ser un año entre 1990 y 2100")
        @Max(value = 2100, message = "debe ser un año entre 1990 y 2100")
        Integer modelo,

        @NotNull(message = "es obligatorio")
        @Min(value = 50, message = "debe estar entre 50 y 1000 c.c.")
        @Max(value = 1000, message = "debe estar entre 50 y 1000 c.c.")
        Integer cilindraje,

        @NotNull(message = "es obligatorio")
        LocalDate vencimientoSoat,

        @NotNull(message = "es obligatorio")
        LocalDate vencimientoTecnomecanica,

        @NotNull(message = "elija el mototaxista dueño de la moto")
        Long conductorId) {
}
