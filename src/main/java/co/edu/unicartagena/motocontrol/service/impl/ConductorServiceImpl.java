package co.edu.unicartagena.motocontrol.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.domain.repository.ConductorRepository;
import co.edu.unicartagena.motocontrol.dto.ConductorRequest;
import co.edu.unicartagena.motocontrol.dto.ConductorResponse;
import co.edu.unicartagena.motocontrol.exception.RecursoDuplicadoException;
import co.edu.unicartagena.motocontrol.exception.RecursoNoEncontradoException;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import co.edu.unicartagena.motocontrol.mapper.ConductorMapper;
import co.edu.unicartagena.motocontrol.service.ConductorService;

@Service
@Transactional
public class ConductorServiceImpl implements ConductorService {

    private final ConductorRepository conductorRepository;
    private final ConductorMapper mapper;

    public ConductorServiceImpl(ConductorRepository conductorRepository, ConductorMapper mapper) {
        this.conductorRepository = conductorRepository;
        this.mapper = mapper;
    }

    @Override
    public ConductorResponse registrar(ConductorRequest request) {
        validarDocumentoUnico(request.numeroDocumento());
        validarLicenciaUnica(request.numeroLicencia());
        Conductor guardado = conductorRepository.save(mapper.aEntidad(request));
        return mapper.aRespuesta(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConductorResponse> listar(EstadoConductor estado) {
        List<Conductor> conductores = estado == null
                ? conductorRepository.findAll()
                : conductorRepository.findByEstado(estado);
        return conductores.stream().map(mapper::aRespuesta).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConductorResponse buscarPorId(Long id) {
        return mapper.aRespuesta(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ConductorResponse buscarPorDocumento(String numeroDocumento) {
        return conductorRepository.findByNumeroDocumento(numeroDocumento)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay ningún mototaxista registrado con el documento " + numeroDocumento + ".",
                        "Revise el número o registre al mototaxista en la sección de mototaxistas."));
    }

    @Override
    public ConductorResponse actualizar(Long id, ConductorRequest request) {
        Conductor conductor = obtener(id);
        if (!conductor.getNumeroDocumento().equals(request.numeroDocumento())) {
            throw new ReglaNegocioException("El número de documento no se puede cambiar.",
                    "Deje el mismo documento. Si quedó mal escrito, avise al administrador del sistema.");
        }
        if (!conductor.getNumeroLicencia().equals(request.numeroLicencia().trim())) {
            validarLicenciaUnica(request.numeroLicencia());
        }
        conductor.actualizarDatos(request.nombres().trim(), request.apellidos().trim(), request.telefono(),
                request.barrio().trim(), request.numeroLicencia().trim(), request.vencimientoLicencia());
        return mapper.aRespuesta(conductorRepository.save(conductor));
    }

    @Override
    public ConductorResponse cambiarEstado(Long id, EstadoConductor estado) {
        Conductor conductor = obtener(id);
        conductor.cambiarEstado(estado);
        return mapper.aRespuesta(conductorRepository.save(conductor));
    }

    /** RN-03: los mototaxistas no se borran; se dan de baja (INACTIVO) para conservar el historial. */
    @Override
    public void inactivar(Long id) {
        cambiarEstado(id, EstadoConductor.INACTIVO);
    }

    private Conductor obtener(Long id) {
        return conductorRepository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.mototaxista(id));
    }

    private void validarDocumentoUnico(String numeroDocumento) {
        if (conductorRepository.existsByNumeroDocumento(numeroDocumento)) {
            throw new RecursoDuplicadoException(
                    "Ya hay un mototaxista registrado con el documento " + numeroDocumento + ".",
                    "Búsquelo en la lista de mototaxistas; si es la misma persona, edite sus datos en vez de "
                            + "registrarla otra vez.");
        }
    }

    private void validarLicenciaUnica(String numeroLicencia) {
        if (conductorRepository.existsByNumeroLicencia(numeroLicencia.trim())) {
            throw new RecursoDuplicadoException(
                    "Ya hay un mototaxista registrado con la licencia " + numeroLicencia.trim() + ".",
                    "Verifique el número de la licencia de conducción; cada licencia se registra una sola vez.");
        }
    }
}
