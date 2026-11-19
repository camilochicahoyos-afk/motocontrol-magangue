package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

/** RN-04: solo un conductor en estado ACTIVO puede prestar servicios. */
@Component
@Order(1)
public class ConductorActivoRegla implements ReglaServicio {

    @Override
    public void validar(ContextoServicio contexto) {
        if (!contexto.conductor().estaActivo()) {
            throw new ReglaNegocioException("El conductor " + contexto.conductor().nombreCompleto()
                    + " no esta activo (estado " + contexto.conductor().getEstado() + ")");
        }
    }
}
