package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConductorRequest(
        @NotBlank(message = "es obligatorio")
        @Pattern(regexp = "\\d{6,10}", message = "debe tener entre 6 y 10 números, sin puntos ni espacios")
        String numeroDocumento,

        @NotBlank(message = "son obligatorios") @Size(max = 60, message = "pueden tener máximo 60 letras")
        String nombres,

        @NotBlank(message = "son obligatorios") @Size(max = 60, message = "pueden tener máximo 60 letras")
        String apellidos,

        @NotBlank(message = "es obligatorio")
        @Pattern(regexp = "3\\d{9}", message = "debe ser un celular de 10 números que empiece por 3")
        String telefono,

        @NotBlank(message = "es obligatorio") @Size(max = 80, message = "puede tener máximo 80 letras")
        String barrio,

        @NotBlank(message = "es obligatorio") @Size(max = 20, message = "puede tener máximo 20 caracteres")
        String numeroLicencia,

        @NotNull(message = "es obligatorio")
        LocalDate vencimientoLicencia) {
}
