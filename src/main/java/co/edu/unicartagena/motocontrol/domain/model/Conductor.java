package co.edu.unicartagena.motocontrol.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Mototaxista registrado en el sistema.
 */
@Entity
@Table(name = "conductores")
public class Conductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 10)
    private String numeroDocumento;

    @Column(nullable = false, length = 60)
    private String nombres;

    @Column(nullable = false, length = 60)
    private String apellidos;

    @Column(nullable = false, length = 10)
    private String telefono;

    @Column(nullable = false, length = 80)
    private String barrio;

    @Column(name = "numero_licencia", nullable = false, unique = true, length = 20)
    private String numeroLicencia;

    @Column(name = "vencimiento_licencia", nullable = false)
    private LocalDate vencimientoLicencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoConductor estado;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    protected Conductor() {
        // requerido por JPA
    }

    public Conductor(String numeroDocumento, String nombres, String apellidos, String telefono,
                     String barrio, String numeroLicencia, LocalDate vencimientoLicencia,
                     LocalDateTime fechaRegistro) {
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.barrio = barrio;
        this.numeroLicencia = numeroLicencia;
        this.vencimientoLicencia = vencimientoLicencia;
        this.fechaRegistro = fechaRegistro;
        this.estado = EstadoConductor.ACTIVO;
    }

    public boolean estaActivo() {
        return estado == EstadoConductor.ACTIVO;
    }

    public boolean tieneLicenciaVigente(LocalDate hoy) {
        return !vencimientoLicencia.isBefore(hoy);
    }

    public void actualizarDatos(String nombres, String apellidos, String telefono, String barrio,
                                String numeroLicencia, LocalDate vencimientoLicencia) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.barrio = barrio;
        this.numeroLicencia = numeroLicencia;
        this.vencimientoLicencia = vencimientoLicencia;
    }

    public void cambiarEstado(EstadoConductor nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    public Long getId() { return id; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getTelefono() { return telefono; }
    public String getBarrio() { return barrio; }
    public String getNumeroLicencia() { return numeroLicencia; }
    public LocalDate getVencimientoLicencia() { return vencimientoLicencia; }
    public EstadoConductor getEstado() { return estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    /** Solo para pruebas y carga controlada. */
    public void asignarId(Long id) { this.id = id; }
}
