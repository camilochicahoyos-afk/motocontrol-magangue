package co.edu.unicartagena.motocontrol.domain.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Motocicleta con la que un conductor presta el servicio.
 */
@Entity
@Table(name = "motocicletas")
public class Motocicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String placa;

    @Column(nullable = false, length = 40)
    private String marca;

    @Column(nullable = false, length = 40)
    private String linea;

    @Column(nullable = false)
    private Integer modelo;

    @Column(nullable = false)
    private Integer cilindraje;

    @Column(name = "vencimiento_soat", nullable = false)
    private LocalDate vencimientoSoat;

    @Column(name = "vencimiento_tecnomecanica", nullable = false)
    private LocalDate vencimientoTecnomecanica;

    @Column(nullable = false)
    private boolean activa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Conductor conductor;

    protected Motocicleta() {
        // requerido por JPA
    }

    public Motocicleta(String placa, String marca, String linea, Integer modelo, Integer cilindraje,
                       LocalDate vencimientoSoat, LocalDate vencimientoTecnomecanica, Conductor conductor) {
        this.placa = placa;
        this.marca = marca;
        this.linea = linea;
        this.modelo = modelo;
        this.cilindraje = cilindraje;
        this.vencimientoSoat = vencimientoSoat;
        this.vencimientoTecnomecanica = vencimientoTecnomecanica;
        this.conductor = conductor;
        this.activa = true;
    }

    public boolean tieneSoatVigente(LocalDate hoy) {
        return !vencimientoSoat.isBefore(hoy);
    }

    public boolean tieneTecnomecanicaVigente(LocalDate hoy) {
        return !vencimientoTecnomecanica.isBefore(hoy);
    }

    public boolean perteneceA(Conductor otro) {
        return conductor != null && otro != null && conductor.getId() != null
                && conductor.getId().equals(otro.getId());
    }

    public void actualizarDocumentos(LocalDate vencimientoSoat, LocalDate vencimientoTecnomecanica) {
        this.vencimientoSoat = vencimientoSoat;
        this.vencimientoTecnomecanica = vencimientoTecnomecanica;
    }

    public void desactivar() {
        this.activa = false;
    }

    public Long getId() { return id; }
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getLinea() { return linea; }
    public Integer getModelo() { return modelo; }
    public Integer getCilindraje() { return cilindraje; }
    public LocalDate getVencimientoSoat() { return vencimientoSoat; }
    public LocalDate getVencimientoTecnomecanica() { return vencimientoTecnomecanica; }
    public boolean isActiva() { return activa; }
    public Conductor getConductor() { return conductor; }

    /** Solo para pruebas y carga controlada. */
    public void asignarId(Long id) { this.id = id; }
}
