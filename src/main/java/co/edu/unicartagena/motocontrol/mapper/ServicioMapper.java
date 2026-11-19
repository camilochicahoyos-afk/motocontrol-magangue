package co.edu.unicartagena.motocontrol.mapper;

import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.Servicio;
import co.edu.unicartagena.motocontrol.dto.ServicioResponse;

@Component
public class ServicioMapper {

    public ServicioResponse aRespuesta(Servicio servicio) {
        return new ServicioResponse(servicio.getId(), servicio.getConductor().getId(),
                servicio.getConductor().nombreCompleto(), servicio.getMotocicleta().getId(),
                servicio.getMotocicleta().getPlaca(), servicio.getBarrioOrigen(), servicio.getBarrioDestino(),
                servicio.getTarifa(), servicio.getFechaHoraInicio(), servicio.getFechaHoraFin(),
                servicio.getEstado());
    }
}
