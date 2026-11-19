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

    private static final String RECURSO = "Conductor";

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
                .orElseThrow(() -> new RecursoNoEncontradoException(RECURSO, numeroDocumento));
    }

    @Override
    public ConductorResponse actualizar(Long id, ConductorRequest request) {
        Conductor conductor = obtener(id);
        if (!conductor.getNumeroDocumento().equals(request.numeroDocumento())) {
            throw new ReglaNegocioException("El numero de documento no se puede modificar");
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

    /** RN-03: los conductores no se borran fisicamente; se inactivan para conservar el historial. */
    @Override
    public void inactivar(Long id) {
        cambiarEstado(id, EstadoConductor.INACTIVO);
    }

    private Conductor obtener(Long id) {
        return conductorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(RECURSO, id));
    }

    private void validarDocumentoUnico(String numeroDocumento) {
        if (conductorRepository.existsByNumeroDocumento(numeroDocumento)) {
            throw new RecursoDuplicadoException("Ya existe un conductor con el documento " + numeroDocumento);
        }
    }

    private void validarLicenciaUnica(String numeroLicencia) {
        if (conductorRepository.existsByNumeroLicencia(numeroLicencia.trim())) {
            throw new RecursoDuplicadoException("Ya existe un conductor con la licencia " + numeroLicencia);
        }
    }
}
