package co.edu.unicartagena.motocontrol.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.dto.CambioEstadoRequest;
import co.edu.unicartagena.motocontrol.dto.ConductorRequest;
import co.edu.unicartagena.motocontrol.dto.ConductorResponse;
import co.edu.unicartagena.motocontrol.service.ConductorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/conductores")
@Tag(name = "Conductores", description = "Registro y gestion de mototaxistas")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @PostMapping
    @Operation(summary = "Registrar un mototaxista")
    public ResponseEntity<ConductorResponse> registrar(@Valid @RequestBody ConductorRequest request) {
        ConductorResponse creado = conductorService.registrar(request);
        return ResponseEntity.created(URI.create("/api/v1/conductores/" + creado.id())).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar mototaxistas, opcionalmente filtrando por estado")
    public List<ConductorResponse> listar(@RequestParam(required = false) EstadoConductor estado) {
        return conductorService.listar(estado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un mototaxista por id")
    public ConductorResponse buscarPorId(@PathVariable Long id) {
        return conductorService.buscarPorId(id);
    }

    @GetMapping("/documento/{numeroDocumento}")
    @Operation(summary = "Consultar un mototaxista por numero de documento")
    public ConductorResponse buscarPorDocumento(@PathVariable String numeroDocumento) {
        return conductorService.buscarPorDocumento(numeroDocumento);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar los datos de un mototaxista")
    public ConductorResponse actualizar(@PathVariable Long id, @Valid @RequestBody ConductorRequest request) {
        return conductorService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado (ACTIVO, SUSPENDIDO, INACTIVO)")
    public ConductorResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return conductorService.cambiarEstado(id, request.estado());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inactivar un mototaxista (borrado logico)")
    public ResponseEntity<Void> inactivar(@PathVariable Long id) {
        conductorService.inactivar(id);
        return ResponseEntity.noContent().build();
    }
}
