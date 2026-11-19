package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;

/** RN-07: la motocicleta debe tener SOAT y revision tecnico-mecanica vigentes. */
@Component
@Order(4)
public class DocumentosMotoVigentesRegla implements ReglaServicio {

    @Override
    public void validar(ContextoServicio contexto) {
        Motocicleta moto = contexto.motocicleta();
        if (!moto.tieneSoatVigente(contexto.fecha())) {
            throw new ReglaNegocioException("El SOAT de la motocicleta " + moto.getPlaca()
                    + " vencio el " + moto.getVencimientoSoat());
        }
        if (!moto.tieneTecnomecanicaVigente(contexto.fecha())) {
            throw new ReglaNegocioException("La revision tecnico-mecanica de la motocicleta " + moto.getPlaca()
                    + " vencio el " + moto.getVencimientoTecnomecanica());
        }
    }
}
