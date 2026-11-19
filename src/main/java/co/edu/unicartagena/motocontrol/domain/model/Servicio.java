package co.edu.unicartagena.motocontrol.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Carrera (viaje) realizada por un mototaxista dentro del municipio.
 */
@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Conductor conductor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "motocicleta_id", nullable = false)
    private Motocicleta motocicleta;

    @Column(name = "barrio_origen", nullable = false, length = 80)
    private String barrioOrigen;

    @Column(name = "barrio_destino", nullable = false, length = 80)
    private String barrioDestino;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifa;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoServicio estado;

    protected Servicio() {
        // requerido por JPA
    }

    public Servicio(Conductor conductor, Motocicleta motocicleta, String barrioOrigen, String barrioDestino,
                    BigDecimal tarifa, LocalDateTime fechaHoraInicio) {
        this.conductor = conductor;
        this.motocicleta = motocicleta;
        this.barrioOrigen = barrioOrigen;
        this.barrioDestino = barrioDestino;
        this.tarifa = tarifa;
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = EstadoServicio.EN_CURSO;
    }

    public void finalizar(LocalDateTime momento) {
        validarEnCurso("finalizar");
        this.fechaHoraFin = momento;
        this.estado = EstadoServicio.FINALIZADO;
    }

    public void cancelar(LocalDateTime momento) {
        validarEnCurso("cancelar");
        this.fechaHoraFin = momento;
        this.estado = EstadoServicio.CANCELADO;
    }

    private void validarEnCurso(String accion) {
        if (estado != EstadoServicio.EN_CURSO) {
            throw new ReglaNegocioException(
                    "No se puede " + accion + " un servicio en estado " + estado);
        }
    }

    public Long getId() { return id; }
    public Conductor getConductor() { return conductor; }
    public Motocicleta getMotocicleta() { return motocicleta; }
    public String getBarrioOrigen() { return barrioOrigen; }
    public String getBarrioDestino() { return barrioDestino; }
    public BigDecimal getTarifa() { return tarifa; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public EstadoServicio getEstado() { return estado; }

    /** Solo para pruebas y carga controlada. */
    public void asignarId(Long id) { this.id = id; }
}
