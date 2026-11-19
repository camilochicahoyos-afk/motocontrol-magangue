package co.edu.unicartagena.motocontrol;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.domain.model.Servicio;
import co.edu.unicartagena.motocontrol.dto.ConductorRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaRequest;
import co.edu.unicartagena.motocontrol.dto.ServicioRequest;

/**
 * Objetos de prueba reutilizables. La fecha "hoy" es fija para que las pruebas no dependan del dia real.
 */
public final class DatosPrueba {

    public static final ZoneId ZONA = ZoneId.of("America/Bogota");
    public static final LocalDate HOY = LocalDate.of(2026, 11, 14);
    public static final Clock RELOJ = Clock.fixed(HOY.atTime(9, 30).atZone(ZONA).toInstant(), ZONA);

    private DatosPrueba() {
    }

    public static Conductor conductor(Long id) {
        Conductor conductor = new Conductor("1052000111", "Luis", "Mercado Perez", "3001234567",
                "Barrio Centro", "LIC-1052000111", HOY.plusYears(2), LocalDateTime.of(2026, 1, 10, 8, 0));
        conductor.asignarId(id);
        return conductor;
    }

    public static Motocicleta motocicleta(Long id, Conductor conductor) {
        Motocicleta moto = new Motocicleta("ABC12D", "Bajaj", "Boxer CT 100", 2022, 100,
                HOY.plusMonths(6), HOY.plusMonths(8), conductor);
        moto.asignarId(id);
        return moto;
    }

    public static ConductorRequest conductorRequest() {
        return new ConductorRequest("1052000111", "Luis", "Mercado Perez", "3001234567",
                "Barrio Centro", "LIC-1052000111", HOY.plusYears(2));
    }

    public static MotocicletaRequest motocicletaRequest(Long conductorId) {
        return new MotocicletaRequest("abc12d", "Bajaj", "Boxer CT 100", 2022, 100,
                HOY.plusMonths(6), HOY.plusMonths(8), conductorId);
    }

    /** Carrera Centro -> Pueblo Nuevo de $3.000 ya finalizada (lista para calificar). */
    public static Servicio carreraFinalizada(Long id, Conductor conductor) {
        Servicio servicio = new Servicio(conductor, motocicleta(10L, conductor), "Centro", "Pueblo Nuevo",
                BigDecimal.valueOf(3000), HOY.atTime(8, 0));
        servicio.asignarId(id);
        servicio.finalizar(HOY.atTime(8, 15));
        return servicio;
    }

    public static ServicioRequest servicioRequest(long tarifa) {
        return new ServicioRequest(1L, 10L, "Barrio Centro", "Barrio Pueblo Nuevo", BigDecimal.valueOf(tarifa));
    }
}
