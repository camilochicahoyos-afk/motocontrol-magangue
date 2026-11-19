package co.edu.unicartagena.motocontrol.controller;

import static co.edu.unicartagena.motocontrol.DatosPrueba.HOY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;
import co.edu.unicartagena.motocontrol.dto.ConductorResponse;
import co.edu.unicartagena.motocontrol.exception.RecursoNoEncontradoException;
import co.edu.unicartagena.motocontrol.service.ConductorService;

@WebMvcTest(ConductorController.class)
class ConductorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConductorService conductorService;

    @Test
    @DisplayName("POST /api/v1/conductores responde 201 con el conductor en JSON")
    void registrarResponde201() throws Exception {
        ConductorResponse respuesta = new ConductorResponse(1L, "1052000111", "Luis", "Mercado Perez",
                "3001234567", "Barrio Centro", "LIC-1052000111", HOY.plusYears(2), true,
                EstadoConductor.ACTIVO, LocalDateTime.of(2026, 11, 14, 9, 30));
        when(conductorService.registrar(any())).thenReturn(respuesta);

        String json = """
                {"numeroDocumento":"1052000111","nombres":"Luis","apellidos":"Mercado Perez",
                 "telefono":"3001234567","barrio":"Barrio Centro","numeroLicencia":"LIC-1052000111",
                 "vencimientoLicencia":"2028-11-14"}
                """;

        mockMvc.perform(post("/api/v1/conductores").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/conductores/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("POST con datos invalidos responde 400 con el detalle de los campos")
    void registrarDatosInvalidos() throws Exception {
        String json = """
                {"numeroDocumento":"12","nombres":"","apellidos":"Mercado",
                 "telefono":"12345","barrio":"Centro","numeroLicencia":"LIC-1",
                 "vencimientoLicencia":"2028-11-14"}
                """;

        mockMvc.perform(post("/api/v1/conductores").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Datos de entrada invalidos"))
                .andExpect(jsonPath("$.detalles.length()").value(3));
    }

    @Test
    @DisplayName("GET de un conductor inexistente responde 404")
    void conductorInexistente() throws Exception {
        when(conductorService.buscarPorId(99L)).thenThrow(new RecursoNoEncontradoException("Conductor", 99L));

        mockMvc.perform(get("/api/v1/conductores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
