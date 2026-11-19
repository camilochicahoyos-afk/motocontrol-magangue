package co.edu.unicartagena.motocontrol.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Muestra fechas y valores como se acostumbra en Colombia (14/11/2026 y $3.000).
 *
 * <p>Se extrajo como clase aparte para no repetir el mismo formato en las reglas y en los servicios
 * (refactorizacion "extraer clase").</p>
 */
public final class Formato {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formato() {
    }

    /** Ejemplo: 2026-11-14 se muestra como 14/11/2026. */
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    /** Ejemplo: 3000 se muestra como $3.000. */
    public static String pesos(BigDecimal valor) {
        if (valor == null) {
            return "";
        }
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.ROOT);
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        return "$" + new DecimalFormat("#,##0", simbolos).format(valor);
    }
}
