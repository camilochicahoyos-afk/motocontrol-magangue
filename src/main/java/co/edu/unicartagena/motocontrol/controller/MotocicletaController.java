package co.edu.unicartagena.motocontrol.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicartagena.motocontrol.dto.DocumentosMotoRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaRequest;
import co.edu.unicartagena.motocontrol.dto.MotocicletaResponse;
import co.edu.unicartagena.motocontrol.service.MotocicletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/motocicletas")
@Tag(name = "Motocicletas", description = "Registro de motocicletas y control de SOAT y tecnico-mecanica")
public class MotocicletaController {

    private final MotocicletaService motocicletaService;

    public MotocicletaController(MotocicletaService motocicletaService) {
        this.motocicletaService = motocicletaService;
    }

    @PostMapping
    @Operation(summary = "Registrar una motocicleta a nombre de un conductor")
    public ResponseEntity<MotocicletaResponse> registrar(@Valid @RequestBody MotocicletaRequest request) {
        MotocicletaResponse creada = motocicletaService.registrar(request);
        return ResponseEntity.created(URI.create("/api/v1/motocicletas/" + creada.id())).body(creada);
    }

    @GetMapping
    @Operation(summary = "Listar motocicletas, opcionalmente por conductor")
    public List<MotocicletaResponse> listar(@RequestParam(required = false) Long conductorId) {
        return motocicletaService.listar(conductorId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una motocicleta por id")
    public MotocicletaResponse buscarPorId(@PathVariable Long id) {
        return motocicletaService.buscarPorId(id);
    }

    @GetMapping("/placa/{placa}")
    @Operation(summary = "Consultar una motocicleta por placa")
    public MotocicletaResponse buscarPorPlaca(@PathVariable String placa) {
        return motocicletaService.buscarPorPlaca(placa);
    }

    @PatchMapping("/{id}/documentos")
    @Operation(summary = "Actualizar las fechas de vencimiento de SOAT y tecnico-mecanica")
    public MotocicletaResponse actualizarDocumentos(@PathVariable Long id,
                                                    @Valid @RequestBody DocumentosMotoRequest request) {
        return motocicletaService.actualizarDocumentos(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar una motocicleta (borrado logico)")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        motocicletaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
