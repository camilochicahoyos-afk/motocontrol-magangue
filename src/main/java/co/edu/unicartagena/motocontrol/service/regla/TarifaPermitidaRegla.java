package co.edu.unicartagena.motocontrol.service.regla;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import co.edu.unicartagena.motocontrol.util.Formato;

/** RN-08: la tarifa debe estar dentro del rango acordado (configurable en application.yml). */
@Component
@Order(5)
public class TarifaPermitidaRegla implements ReglaServicio {

    private final BigDecimal tarifaMinima;
    private final BigDecimal tarifaMaxima;

    public TarifaPermitidaRegla(@Value("${motocontrol.tarifa.minima:2000}") BigDecimal tarifaMinima,
                                @Value("${motocontrol.tarifa.maxima:15000}") BigDecimal tarifaMaxima) {
        this.tarifaMinima = tarifaMinima;
        this.tarifaMaxima = tarifaMaxima;
    }

    @Override
    public void validar(ContextoServicio contexto) {
        BigDecimal tarifa = contexto.tarifa();
        if (tarifa.compareTo(tarifaMinima) < 0 || tarifa.compareTo(tarifaMaxima) > 0) {
            throw new ReglaNegocioException("La tarifa de " + Formato.pesos(tarifa)
                    + " está fuera del rango permitido.",
                    "Cobre entre " + Formato.pesos(tarifaMinima) + " y " + Formato.pesos(tarifaMaxima) + " pesos.");
        }
    }
}
