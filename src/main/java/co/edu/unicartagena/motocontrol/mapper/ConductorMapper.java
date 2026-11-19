package co.edu.unicartagena.motocontrol.mapper;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.dto.ConductorRequest;
import co.edu.unicartagena.motocontrol.dto.ConductorResponse;

/**
 * Convierte entre la entidad Conductor y sus DTO; la API nunca expone entidades JPA.
 */
@Component
public class ConductorMapper {

    private final Clock clock;

    public ConductorMapper(Clock clock) {
        this.clock = clock;
    }

    public Conductor aEntidad(ConductorRequest request) {
        return new Conductor(request.numeroDocumento(), request.nombres().trim(), request.apellidos().trim(),
                request.telefono(), request.barrio().trim(), request.numeroLicencia().trim(),
                request.vencimientoLicencia(), LocalDateTime.now(clock));
    }

    public ConductorResponse aRespuesta(Conductor conductor) {
        return new ConductorResponse(conductor.getId(), conductor.getNumeroDocumento(), conductor.getNombres(),
                conductor.getApellidos(), conductor.getTelefono(), conductor.getBarrio(),
                conductor.getNumeroLicencia(), conductor.getVencimientoLicencia(),
                conductor.tieneLicenciaVigente(LocalDate.now(clock)), conductor.getEstado(),
                conductor.getFechaRegistro());
    }
}
