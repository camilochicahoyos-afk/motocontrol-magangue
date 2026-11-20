package co.edu.unicartagena.motocontrol.config;

import co.edu.unicartagena.motocontrol.domain.model.AspectoServicio;
import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.dto.CalificacionRequest;
import co.edu.unicartagena.motocontrol.dto.ConductorRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaRequest;
import co.edu.unicartagena.motocontrol.dto.ServicioRequest;
import co.edu.unicartagena.motocontrol.service.CalificacionService;
import co.edu.unicartagena.motocontrol.service.ConductorService;
import co.edu.unicartagena.motocontrol.service.MotocicletaService;
import co.edu.unicartagena.motocontrol.service.ServicioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Set;

import static co.edu.unicartagena.motocontrol.domain.model.AspectoServicio.*;

/**
 * Datos de prueba para la versión publicada: con el perfil "demo" la aplicación arranca con cuatro
 * mototaxistas, cinco motos, carreras del día y calificaciones, para que la página se pueda recorrer
 * sin registrar nada. Todo pasa por los servicios, así que se cumplen las mismas reglas de negocio
 * que en la página, y las fechas se calculan a partir del día en que arranca.
 */
@Component
@Profile("demo")
public class DatosDemo implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DatosDemo.class);

    private static final String[] BARRIOS = {"Centro", "Pueblo Nuevo", "Alfonso López", "Majagual", "Baracoa",
            "La Esperanza", "Las Brisas", "San José", "El Recreo", "Camilo Torres", "Montecarlo", "Puerto"};
    private static final int[] TARIFAS = {3000, 2500, 3500, 4000, 3000, 5000, 2500, 3000, 3500};

    private final ConductorService conductores;
    private final MotocicletaService motocicletas;
    private final ServicioService servicios;
    private final CalificacionService calificaciones;
    private final Clock clock;
    private int numeroCarrera;

    public DatosDemo(ConductorService conductores, MotocicletaService motocicletas, ServicioService servicios,
                     CalificacionService calificaciones, Clock clock) {
        this.conductores = conductores;
        this.motocicletas = motocicletas;
        this.servicios = servicios;
        this.calificaciones = calificaciones;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!conductores.listar(null).isEmpty()) {
            return;
        }
        LocalDate hoy = LocalDate.now(clock);

        Long luis = conductor("1052000111", "Luis Alberto", "Mercado Pérez", "3001234567", "Centro",
                hoy.plusYears(2));
        Long carlos = conductor("1052334455", "Carlos Andrés", "Rivero Díaz", "3014567890", "Pueblo Nuevo",
                hoy.plusYears(2).plusMonths(4));
        Long jose = conductor("73145678", "José Miguel", "Ortega Barrios", "3157894561", "Alfonso López",
                hoy.plusMonths(8));
        Long andres = conductor("1052987654", "Andrés Felipe", "Gil Torres", "3209876543", "Yatí",
                hoy.minusDays(49));

        Long abc = moto("ABC12D", "Bajaj", "Boxer CT 100", 2022, 100, hoy.plusMonths(6), hoy.plusMonths(3), luis);
        Long def = moto("DEF34G", "AKT", "NKD 125", 2021, 125, hoy.plusMonths(2), hoy.plusMonths(1), carlos);
        moto("XYZ45E", "Honda", "CB 110", 2019, 110, hoy.minusDays(18), hoy.plusMonths(4), carlos);
        Long ghi = moto("GHI56J", "Suzuki", "AX 100", 2018, 100, hoy.plusMonths(5), hoy.plusMonths(1), jose);
        moto("KLM78N", "Yamaha", "YBR 125", 2020, 125, hoy.plusMonths(7), hoy.plusMonths(2), andres);

        // Luis: 12 calificaciones, promedio 4,6
        calificar(luis, abc, 5, "Muy responsable, me dio casco y manejó con cuidado.",
                CONDUCE_CON_PRECAUCION, AMABLE, OFRECIO_CASCO);
        calificar(luis, abc, 5, null, CONDUCE_CON_PRECAUCION, AMABLE);
        calificar(luis, abc, 5, "Excelente servicio, cobró lo justo.",
                CONDUCE_CON_PRECAUCION, OFRECIO_CASCO, COBRO_LO_ACORDADO);
        calificar(luis, abc, 5, null, CONDUCE_CON_PRECAUCION, AMABLE, OFRECIO_CASCO);
        calificar(luis, abc, 5, "Puntual y muy amable.", CONDUCE_CON_PRECAUCION, AMABLE);
        calificar(luis, abc, 5, null, CONDUCE_CON_PRECAUCION, OFRECIO_CASCO);
        calificar(luis, abc, 5, null, CONDUCE_CON_PRECAUCION, AMABLE, COBRO_LO_ACORDADO);
        calificar(luis, abc, 5, "Siempre ofrece casco.", CONDUCE_CON_PRECAUCION, OFRECIO_CASCO);
        calificar(luis, abc, 4, null, AMABLE, PUNTUAL);
        calificar(luis, abc, 4, "Buen servicio.", CONDUCE_CON_PRECAUCION, COBRO_LO_ACORDADO);
        calificar(luis, abc, 4, null, AMABLE, PUNTUAL);
        calificar(luis, abc, 3, "Manejó bien, pero se demoró en llegar.", CONDUCE_CON_PRECAUCION, SE_DEMORO);

        // Carlos: 9 calificaciones, promedio 4,1
        calificar(carlos, def, 5, "Llegó rapidísimo y conoce todos los barrios.", PUNTUAL, CONOCE_LOS_BARRIOS);
        calificar(carlos, def, 5, null, PUNTUAL, CONOCE_LOS_BARRIOS);
        calificar(carlos, def, 5, null, PUNTUAL, AMABLE);
        calificar(carlos, def, 5, null, PUNTUAL, CONOCE_LOS_BARRIOS, AMABLE);
        calificar(carlos, def, 4, "Rápido, pero a veces va muy acelerado.", PUNTUAL, MUY_RAPIDO);
        calificar(carlos, def, 4, null, PUNTUAL);
        calificar(carlos, def, 4, null, CONOCE_LOS_BARRIOS);
        calificar(carlos, def, 3, null, MUY_RAPIDO);
        calificar(carlos, def, 2, "Me cobró más de lo que habíamos acordado.", COBRO_DE_MAS);

        // José: 5 calificaciones, promedio 2,4, queda para revisión y la asociación lo suspende
        calificar(jose, ghi, 4, null, AMABLE);
        calificar(jose, ghi, 3, "No tenía casco para el pasajero.", NO_OFRECIO_CASCO);
        calificar(jose, ghi, 2, "Iba muy rápido y sin casco para mí.", NO_OFRECIO_CASCO, MUY_RAPIDO);
        calificar(jose, ghi, 2, null, NO_OFRECIO_CASCO, MUY_RAPIDO, COBRO_DE_MAS);
        calificar(jose, ghi, 1, "Trato grosero y cobró de más.",
                NO_OFRECIO_CASCO, MUY_RAPIDO, COBRO_DE_MAS, TRATO_IRRESPETUOSO);
        conductores.cambiarEstado(jose, EstadoConductor.SUSPENDIDO);

        // Otras carreras del día: una cancelada, una finalizada sin calificar y una en curso
        servicios.cancelar(carrera(carlos, def));
        servicios.finalizar(carrera(carlos, def));
        carrera(luis, abc);

        LOG.info("Datos de prueba cargados: 4 mototaxistas, 5 motos y {} carreras", numeroCarrera);
    }

    private Long conductor(String documento, String nombres, String apellidos, String telefono, String barrio,
                           LocalDate vencimientoLicencia) {
        return conductores.registrar(new ConductorRequest(documento, nombres, apellidos, telefono, barrio,
                documento, vencimientoLicencia)).id();
    }

    private Long moto(String placa, String marca, String linea, int modelo, int cilindraje, LocalDate soat,
                      LocalDate tecnomecanica, Long conductorId) {
        return motocicletas.registrar(new MotocicletaRequest(placa, marca, linea, modelo, cilindraje, soat,
                tecnomecanica, conductorId)).id();
    }

    private Long carrera(Long conductorId, Long motocicletaId) {
        String origen = BARRIOS[numeroCarrera % BARRIOS.length];
        String destino = BARRIOS[(numeroCarrera * 5 + 3) % BARRIOS.length];
        if (origen.equals(destino)) {
            destino = origen.equals("Centro") ? "Puerto" : "Centro";
        }
        BigDecimal tarifa = BigDecimal.valueOf(TARIFAS[numeroCarrera % TARIFAS.length]);
        numeroCarrera++;
        return servicios.registrar(new ServicioRequest(conductorId, motocicletaId, origen, destino, tarifa)).id();
    }

    private void calificar(Long conductorId, Long motocicletaId, int estrellas, String comentario,
                           AspectoServicio... aspectos) {
        Long carrera = carrera(conductorId, motocicletaId);
        servicios.finalizar(carrera);
        calificaciones.calificar(new CalificacionRequest(carrera, estrellas, Set.of(aspectos), comentario));
    }
}
