package co.edu.unicartagena.motocontrol.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;

public record ConductorResponse(
        Long id,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String telefono,
        String barrio,
        String numeroLicencia,
        LocalDate vencimientoLicencia,
        boolean licenciaVigente,
        EstadoConductor estado,
        LocalDateTime fechaRegistro) {
}
