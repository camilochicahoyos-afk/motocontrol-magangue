package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import co.edu.unicartagena.motocontrol.util.Formato;

/** RN-05: el mototaxista debe tener la licencia de conduccion vigente el dia de la carrera. */
@Component
@Order(2)
public class LicenciaVigenteRegla implements ReglaServicio {

    @Override
    public void validar(ContextoServicio contexto) {
        Conductor conductor = contexto.conductor();
        if (!conductor.tieneLicenciaVigente(contexto.fecha())) {
            throw new ReglaNegocioException("La licencia de conducción de " + conductor.nombreCompleto()
                    + " venció el " + Formato.fecha(conductor.getVencimientoLicencia()) + ".",
                    "Renueve la licencia y actualice la fecha con el botón Editar de la sección de mototaxistas.");
        }
    }
}
