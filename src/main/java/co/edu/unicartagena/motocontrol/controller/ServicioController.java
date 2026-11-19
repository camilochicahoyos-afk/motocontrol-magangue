package co.edu.unicartagena.motocontrol.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicartagena.motocontrol.dto.ResumenDiarioResponse;
import co.edu.unicartagena.motocontrol.dto.ServicioRequest;
import co.edu.unicartagena.motocontrol.dto.ServicioResponse;
import co.edu.unicartagena.motocontrol.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/servicios")
@Tag(name = "Servicios", description = "Registro de carreras y reportes diarios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @PostMapping
    @Operation(summary = "Iniciar un servicio (valida las reglas de negocio RN-04 a RN-09)")
    public ResponseEntity<ServicioResponse> registrar(@Valid @RequestBody ServicioRequest request) {
        ServicioResponse creado = servicioService.registrar(request);
        return ResponseEntity.created(URI.create("/api/v1/servicios/" + creado.id())).body(creado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un servicio por id")
    public ServicioResponse buscarPorId(@PathVariable Long id) {
        return servicioService.buscarPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar los servicios de un conductor")
    public List<ServicioResponse> listarPorConductor(@RequestParam Long conductorId) {
        return servicioService.listarPorConductor(conductorId);
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar un servicio en curso")
    public ServicioResponse finalizar(@PathVariable Long id) {
        return servicioService.finalizar(id);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar un servicio en curso")
    public ServicioResponse cancelar(@PathVariable Long id) {
        return servicioService.cancelar(id);
    }

    @GetMapping("/resumen")
    @Operation(summary = "Resumen de servicios e ingresos de un dia (yyyy-MM-dd)")
    public ResumenDiarioResponse resumenDiario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return servicioService.resumenDiario(fecha);
    }
}
