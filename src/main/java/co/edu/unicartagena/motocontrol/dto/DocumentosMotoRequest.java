package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record DocumentosMotoRequest(
        @NotNull LocalDate vencimientoSoat,
        @NotNull LocalDate vencimientoTecnomecanica) {
}
