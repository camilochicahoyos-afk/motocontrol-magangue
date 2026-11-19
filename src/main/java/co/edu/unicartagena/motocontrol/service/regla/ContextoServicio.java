package co.edu.unicartagena.motocontrol.service.regla;

import java.math.BigDecimal;
import java.time.LocalDate;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;

/**
 * Datos que las reglas de negocio necesitan para decidir si un servicio puede iniciarse.
 */
public record ContextoServicio(Conductor conductor, Motocicleta motocicleta, BigDecimal tarifa, LocalDate fecha) {
}
