package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.EstadoServicio;
import co.edu.unicartagena.motocontrol.domain.repository.ServicioRepository;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

/** RN-09: un conductor no puede tener dos servicios en curso al mismo tiempo. */
@Component
@Order(6)
public class UnServicioEnCursoRegla implements ReglaServicio {

    private final ServicioRepository servicioRepository;

    public UnServicioEnCursoRegla(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Override
    public void validar(ContextoServicio contexto) {
        if (servicioRepository.existsByConductorIdAndEstado(contexto.conductor().getId(), EstadoServicio.EN_CURSO)) {
            throw new ReglaNegocioException("El conductor ya tiene un servicio en curso; debe finalizarlo o cancelarlo");
        }
    }
}
