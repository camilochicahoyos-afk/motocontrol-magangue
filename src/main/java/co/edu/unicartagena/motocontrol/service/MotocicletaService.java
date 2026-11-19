package co.edu.unicartagena.motocontrol.service;

import java.util.List;

import co.edu.unicartagena.motocontrol.dto.DocumentosMotoRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaResponse;

public interface MotocicletaService {

    MotocicletaResponse registrar(MotocicletaRequest request);

    List<MotocicletaResponse> listar(Long conductorId);

    MotocicletaResponse buscarPorId(Long id);

    MotocicletaResponse buscarPorPlaca(String placa);

    MotocicletaResponse actualizarDocumentos(Long id, DocumentosMotoRequest request);

    void desactivar(Long id);
}
