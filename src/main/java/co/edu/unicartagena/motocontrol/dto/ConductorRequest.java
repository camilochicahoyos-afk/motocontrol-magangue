package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConductorRequest(
        @NotBlank @Pattern(regexp = "\\d{6,10}", message = "debe tener entre 6 y 10 digitos")
        String numeroDocumento,

        @NotBlank @Size(max = 60)
        String nombres,

        @NotBlank @Size(max = 60)
        String apellidos,

        @NotBlank @Pattern(regexp = "3\\d{9}", message = "debe ser un celular colombiano de 10 digitos")
        String telefono,

        @NotBlank @Size(max = 80)
        String barrio,

        @NotBlank @Size(max = 20)
        String numeroLicencia,

        @NotNull
        LocalDate vencimientoLicencia) {
}
