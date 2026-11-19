package co.edu.unicartagena.motocontrol.service.regla;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import co.edu.unicartagena.motocontrol.util.Formato;

/** RN-07: la motocicleta debe tener SOAT y revision tecnico-mecanica vigentes. */
@Component
@Order(4)
public class DocumentosMotoVigentesRegla implements ReglaServicio {

    @Override
    public void validar(ContextoServicio contexto) {
        Motocicleta moto = contexto.motocicleta();
        if (!moto.tieneSoatVigente(contexto.fecha())) {
            throw new ReglaNegocioException("El SOAT de la motocicleta " + moto.getPlaca()
                    + " venció el " + Formato.fecha(moto.getVencimientoSoat()) + ".",
                    "Renueve el SOAT y actualice la fecha en la sección de motocicletas.");
        }
        if (!moto.tieneTecnomecanicaVigente(contexto.fecha())) {
            throw new ReglaNegocioException("La revisión técnico-mecánica de la motocicleta " + moto.getPlaca()
                    + " venció el " + Formato.fecha(moto.getVencimientoTecnomecanica()) + ".",
                    "Haga la revisión técnico-mecánica y actualice la fecha en la sección de motocicletas.");
        }
    }
}
