package co.edu.unicartagena.motocontrol.dto;

import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(@NotNull(message = "es obligatorio") EstadoConductor estado) {
}
