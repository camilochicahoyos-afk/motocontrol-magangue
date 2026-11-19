package co.edu.unicartagena.motocontrol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumenDiarioResponse(
        LocalDate fecha,
        long totalServicios,
        long finalizados,
        long cancelados,
        long enCurso,
        BigDecimal ingresosFinalizados) {
}
