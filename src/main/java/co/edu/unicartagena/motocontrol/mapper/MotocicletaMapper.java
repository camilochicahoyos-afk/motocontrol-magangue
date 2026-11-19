package co.edu.unicartagena.motocontrol.mapper;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;

import org.springframework.stereotype.Component;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.dto.MotocicletaRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaResponse;

@Component
public class MotocicletaMapper {

    private final Clock clock;

    public MotocicletaMapper(Clock clock) {
        this.clock = clock;
    }

    public Motocicleta aEntidad(MotocicletaRequest request, Conductor conductor) {
        return new Motocicleta(normalizarPlaca(request.placa()), request.marca().trim(), request.linea().trim(),
                request.modelo(), request.cilindraje(), request.vencimientoSoat(),
                request.vencimientoTecnomecanica(), conductor);
    }

    public MotocicletaResponse aRespuesta(Motocicleta moto) {
        LocalDate hoy = LocalDate.now(clock);
        boolean documentosVigentes = moto.tieneSoatVigente(hoy) && moto.tieneTecnomecanicaVigente(hoy);
        Conductor conductor = moto.getConductor();
        return new MotocicletaResponse(moto.getId(), moto.getPlaca(), moto.getMarca(), moto.getLinea(),
                moto.getModelo(), moto.getCilindraje(), moto.getVencimientoSoat(),
                moto.getVencimientoTecnomecanica(), documentosVigentes, moto.isActiva(),
                conductor.getId(), conductor.nombreCompleto());
    }

    public static String normalizarPlaca(String placa) {
        return placa.trim().toUpperCase(Locale.ROOT);
    }
}
