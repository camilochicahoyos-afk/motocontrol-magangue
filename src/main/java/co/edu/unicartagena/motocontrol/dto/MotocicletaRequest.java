package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MotocicletaRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}\\d{2}[A-Za-z]", message = "formato de placa de moto invalido (ej. ABC12D)")
        String placa,

        @NotBlank @Size(max = 40)
        String marca,

        @NotBlank @Size(max = 40)
        String linea,

        @NotNull @Min(1990) @Max(2100)
        Integer modelo,

        @NotNull @Min(50) @Max(1000)
        Integer cilindraje,

        @NotNull
        LocalDate vencimientoSoat,

        @NotNull
        LocalDate vencimientoTecnomecanica,

        @NotNull
        Long conductorId) {
}
