package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record DocumentosMotoRequest(
        @NotNull(message = "es obligatorio") LocalDate vencimientoSoat,
        @NotNull(message = "es obligatorio") LocalDate vencimientoTecnomecanica) {
}
