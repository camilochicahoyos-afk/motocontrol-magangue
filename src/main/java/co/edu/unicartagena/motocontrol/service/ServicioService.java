package co.edu.unicartagena.motocontrol.service;

import java.time.LocalDate;
import java.util.List;

import co.edu.unicartagena.motocontrol.dto.ResumenDiarioResponse;
import co.edu.unicartagena.motocontrol.dto.ServicioRequest;
import co.edu.unicartagena.motocontrol.dto.ServicioResponse;

public interface ServicioService {

    ServicioResponse registrar(ServicioRequest request);

    ServicioResponse buscarPorId(Long id);

    List<ServicioResponse> listarPorConductor(Long conductorId);

    ServicioResponse finalizar(Long id);

    ServicioResponse cancelar(Long id);

    ResumenDiarioResponse resumenDiario(LocalDate fecha);
}
