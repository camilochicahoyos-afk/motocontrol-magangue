package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

/** RN-06: la motocicleta debe estar activa y registrada a nombre del mototaxista de la carrera. */
@Component
@Order(3)
public class MotocicletaAsignadaRegla implements ReglaServicio {

    @Override
    public void validar(ContextoServicio contexto) {
        Motocicleta moto = contexto.motocicleta();
        if (!moto.isActiva()) {
            throw new ReglaNegocioException("La motocicleta " + moto.getPlaca() + " está desactivada.",
                    "Use otra motocicleta activa del mototaxista.");
        }
        if (!moto.perteneceA(contexto.conductor())) {
            throw new ReglaNegocioException("La motocicleta " + moto.getPlaca() + " no está registrada a nombre de "
                    + contexto.conductor().nombreCompleto() + ".",
                    "Elija una de las motocicletas de este mototaxista o registre la moto a su nombre.");
        }
    }
}
