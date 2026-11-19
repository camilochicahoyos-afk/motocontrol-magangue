package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

/** RN-05: el conductor debe tener la licencia de conduccion vigente el dia del servicio. */
@Component
@Order(2)
public class LicenciaVigenteRegla implements ReglaServicio {

    @Override
    public void validar(ContextoServicio contexto) {
        if (!contexto.conductor().tieneLicenciaVigente(contexto.fecha())) {
            throw new ReglaNegocioException("La licencia de conduccion vencio el "
                    + contexto.conductor().getVencimientoLicencia());
        }
    }
}
