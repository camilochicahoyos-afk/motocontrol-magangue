package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

/** RN-06: la motocicleta debe estar activa y registrada a nombre del conductor que presta el servicio. */
@Component
@Order(3)
public class MotocicletaAsignadaRegla implements ReglaServicio {

    @Override
    public void validar(ContextoServicio contexto) {
        if (!contexto.motocicleta().isActiva()) {
            throw new ReglaNegocioException("La motocicleta " + contexto.motocicleta().getPlaca() + " esta inactiva");
        }
        if (!contexto.motocicleta().perteneceA(contexto.conductor())) {
            throw new ReglaNegocioException("La motocicleta " + contexto.motocicleta().getPlaca()
                    + " no esta asignada al conductor indicado");
        }
    }
}
