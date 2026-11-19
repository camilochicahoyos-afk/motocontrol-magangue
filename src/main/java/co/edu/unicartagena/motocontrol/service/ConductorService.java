package co.edu.unicartagena.motocontrol.service;

import java.util.List;

import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.dto.ConductorRequest;
import co.edu.unicartagena.motocontrol.dto.ConductorResponse;

/**
 * Casos de uso sobre mototaxistas. Los controladores dependen de esta abstraccion y no de la
 * implementacion (principio de inversion de dependencias, DIP).
 */
public interface ConductorService {

    ConductorResponse registrar(ConductorRequest request);

    List<ConductorResponse> listar(EstadoConductor estado);

    ConductorResponse buscarPorId(Long id);

    ConductorResponse buscarPorDocumento(String numeroDocumento);

    ConductorResponse actualizar(Long id, ConductorRequest request);

    ConductorResponse cambiarEstado(Long id, EstadoConductor estado);

    void inactivar(Long id);
}
