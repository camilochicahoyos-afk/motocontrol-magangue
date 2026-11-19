package co.edu.unicartagena.motocontrol.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicartagena.motocontrol.dto.CalificacionRequest;
import co.edu.unicartagena.motocontrol.dto.CalificacionResponse;
import co.edu.unicartagena.motocontrol.dto.OpcionesCalificacionResponse;
import co.edu.unicartagena.motocontrol.dto.OpinionesConductorResponse;
import co.edu.unicartagena.motocontrol.dto.ReputacionResponse;
import co.edu.unicartagena.motocontrol.service.CalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/calificaciones")
@Tag(name = "Calificaciones", description = "Opinion de los pasajeros sobre los mototaxistas")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping
    @Operation(summary = "Calificar una carrera finalizada de 1 a 5 estrellas (RN-11 y RN-12)")
    public ResponseEntity<CalificacionResponse> calificar(@Valid @RequestBody CalificacionRequest request) {
        CalificacionResponse creada = calificacionService.calificar(request);
        return ResponseEntity.created(URI.create("/api/v1/calificaciones/" + creada.id())).body(creada);
    }

    @GetMapping("/opciones")
    @Operation(summary = "Escala de estrellas y opciones rapidas para calificar")
    public OpcionesCalificacionResponse opciones() {
        return calificacionService.opciones();
    }

    @GetMapping("/conductores/{conductorId}")
    @Operation(summary = "Reputacion y opiniones de un mototaxista")
    public OpinionesConductorResponse reputacion(@PathVariable Long conductorId) {
        return calificacionService.reputacion(conductorId);
    }

    @GetMapping("/ranking")
    @Operation(summary = "Ranking de mototaxistas por promedio; marca \"para revision\" (RN-13)")
    public List<ReputacionResponse> ranking() {
        return calificacionService.ranking();
    }
}
