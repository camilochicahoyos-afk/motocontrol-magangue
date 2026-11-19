package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

/** RN-04: solo un mototaxista en estado ACTIVO puede hacer carreras. */
@Component
@Order(1)
public class ConductorActivoRegla implements ReglaServicio {

    @Override
    public void validar(ContextoServicio contexto) {
        Conductor conductor = contexto.conductor();
        if (!conductor.estaActivo()) {
            throw new ReglaNegocioException(conductor.nombreCompleto() + " está "
                    + conductor.getEstado().getDescripcion() + " y no puede hacer carreras.",
                    "Solo los mototaxistas activos pueden hacer carreras. Si ya resolvió su situación, "
                            + "actívelo en la sección de mototaxistas.");
        }
    }
}
