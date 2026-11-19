package co.edu.unicartagena.motocontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Datos de la documentacion tecnica (Swagger UI) para los desarrolladores. El usuario final usa la
 * pagina en espanol de http://localhost:8080.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacion() {
        return new OpenAPI().info(new Info()
                .title("MotoControl Magangué - documentación técnica de la API")
                .version("1.0")
                .description("API REST para el registro y control del mototaxismo en Magangué, Bolívar: "
                        + "mototaxistas, motocicletas, carreras y calificaciones. Cada error responde con "
                        + "un mensaje (qué pasó) y una solución (cómo corregirlo)."));
    }
}
