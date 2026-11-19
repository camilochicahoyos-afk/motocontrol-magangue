package co.edu.unicartagena.motocontrol.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;
import co.edu.unicartagena.motocontrol.domain.repository.ConductorRepository;
import co.edu.unicartagena.motocontrol.domain.repository.MotocicletaRepository;
import co.edu.unicartagena.motocontrol.dto.DocumentosMotoRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaResponse;
import co.edu.unicartagena.motocontrol.exception.RecursoDuplicadoException;
import co.edu.unicartagena.motocontrol.exception.RecursoNoEncontradoException;
import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import co.edu.unicartagena.motocontrol.mapper.MotocicletaMapper;
import co.edu.unicartagena.motocontrol.service.MotocicletaService;

@Service
@Transactional
public class MotocicletaServiceImpl implements MotocicletaService {

    /** RN-02: un mototaxista puede tener como maximo dos motocicletas activas. */
    static final int MAXIMO_MOTOS_ACTIVAS = 2;

    private final MotocicletaRepository motocicletaRepository;
    private final ConductorRepository conductorRepository;
    private final MotocicletaMapper mapper;

    public MotocicletaServiceImpl(MotocicletaRepository motocicletaRepository,
                                  ConductorRepository conductorRepository,
                                  MotocicletaMapper mapper) {
        this.motocicletaRepository = motocicletaRepository;
        this.conductorRepository = conductorRepository;
        this.mapper = mapper;
    }

    @Override
    public MotocicletaResponse registrar(MotocicletaRequest request) {
        String placa = MotocicletaMapper.normalizarPlaca(request.placa());
        if (motocicletaRepository.existsByPlaca(placa)) {
            throw new RecursoDuplicadoException("Ya hay una motocicleta registrada con la placa " + placa + ".",
                    "Verifique la placa; cada motocicleta se registra una sola vez.");
        }
        Conductor conductor = conductorRepository.findById(request.conductorId())
                .orElseThrow(() -> RecursoNoEncontradoException.mototaxista(request.conductorId()));
        if (conductor.getEstado() == EstadoConductor.INACTIVO) {
            throw new ReglaNegocioException("No se le pueden registrar motocicletas a "
                    + conductor.nombreCompleto() + " porque está dado de baja.",
                    "Actívelo de nuevo en la sección de mototaxistas y luego registre la moto.");
        }
        if (motocicletaRepository.countByConductorIdAndActivaTrue(conductor.getId()) >= MAXIMO_MOTOS_ACTIVAS) {
            throw new ReglaNegocioException(conductor.nombreCompleto() + " ya tiene " + MAXIMO_MOTOS_ACTIVAS
                    + " motocicletas activas, que es el máximo permitido.",
                    "Desactive una de sus motocicletas antes de registrar otra.");
        }
        Motocicleta guardada = motocicletaRepository.save(mapper.aEntidad(request, conductor));
        return mapper.aRespuesta(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MotocicletaResponse> listar(Long conductorId) {
        List<Motocicleta> motos = conductorId == null
                ? motocicletaRepository.findAll()
                : motocicletaRepository.findByConductorId(conductorId);
        return motos.stream().map(mapper::aRespuesta).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MotocicletaResponse buscarPorId(Long id) {
        return mapper.aRespuesta(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public MotocicletaResponse buscarPorPlaca(String placa) {
        String normalizada = MotocicletaMapper.normalizarPlaca(placa);
        return motocicletaRepository.findByPlaca(normalizada)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay ninguna motocicleta registrada con la placa " + normalizada + ".",
                        "Revise la placa o registre la motocicleta en la sección de motocicletas."));
    }

    @Override
    public MotocicletaResponse actualizarDocumentos(Long id, DocumentosMotoRequest request) {
        Motocicleta moto = obtener(id);
        moto.actualizarDocumentos(request.vencimientoSoat(), request.vencimientoTecnomecanica());
        return mapper.aRespuesta(motocicletaRepository.save(moto));
    }

    @Override
    public void desactivar(Long id) {
        Motocicleta moto = obtener(id);
        moto.desactivar();
        motocicletaRepository.save(moto);
    }

    private Motocicleta obtener(Long id) {
        return motocicletaRepository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.motocicleta(id));
    }
}
