package co.edu.unicartagena.motocontrol.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expone un reloj inyectable para que las reglas que dependen de la fecha
 * (vencimiento de licencia, SOAT, tecnomecanica) se puedan probar sin depender del dia real.
 */
@Configuration
public class ClockConfig {

    public static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

    @Bean
    public Clock clock() {
        return Clock.system(ZONA_COLOMBIA);
    }
}
