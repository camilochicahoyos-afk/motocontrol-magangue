package co.edu.unicartagena.motocontrol.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.edu.unicartagena.motocontrol.exception.ReglaNegocioException;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Opinion del pasajero sobre una carrera: de 1 a 5 estrellas, opciones rapidas y un comentario.
 */
@Entity
@Table(name = "calificaciones")
public class Calificacion {

    /** Palabra que corresponde a cada numero de estrellas (posicion 0 = 1 estrella). */
    private static final List<String> ESCALA = List.of("Muy malo", "Malo", "Regular", "Bueno", "Excelente");

    public static final int MAXIMO_COMENTARIO = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false, unique = true)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Conductor conductor;

    @Column(nullable = false)
    private int estrellas;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "calificacion_aspectos", joinColumns = @JoinColumn(name = "calificacion_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "aspecto", length = 30)
    private Set<AspectoServicio> aspectos = new HashSet<>();

    @Column(length = MAXIMO_COMENTARIO)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected Calificacion() {
        // requerido por JPA
    }

    /**
     * RN-11: solo se califican carreras finalizadas. La calificacion queda asociada al mototaxista
     * que hizo la carrera.
     */
    public Calificacion(Servicio servicio, int estrellas, Set<AspectoServicio> aspectos, String comentario,
                        LocalDateTime fecha) {
        if (!servicio.estaFinalizado()) {
            throw new ReglaNegocioException(
                    "Solo se pueden calificar carreras finalizadas y esta carrera está "
                            + servicio.getEstado().getDescripcion() + ".",
                    "Finalice la carrera y luego califíquela.");
        }
        if (estrellas < 1 || estrellas > 5) {
            throw new ReglaNegocioException("La calificación debe ser de 1 a 5 estrellas.",
                    "Toque entre una y cinco estrellas.");
        }
        this.servicio = servicio;
        this.conductor = servicio.getConductor();
        this.estrellas = estrellas;
        if (aspectos != null && !aspectos.isEmpty()) {
            this.aspectos = new HashSet<>(aspectos);
        }
        this.comentario = comentario == null || comentario.isBlank() ? null : comentario.trim();
        this.fecha = fecha;
    }

    /** Ejemplo: 4 estrellas = "Bueno". */
    public static String valoracion(int estrellas) {
        return ESCALA.get(estrellas - 1);
    }

    public static List<String> escala() {
        return ESCALA;
    }

    public Long getId() { return id; }
    public Servicio getServicio() { return servicio; }
    public Conductor getConductor() { return conductor; }
    public int getEstrellas() { return estrellas; }
    public Set<AspectoServicio> getAspectos() { return Collections.unmodifiableSet(aspectos); }
    public String getComentario() { return comentario; }
    public LocalDateTime getFecha() { return fecha; }

    /** Solo para pruebas y carga controlada. */
    public void asignarId(Long id) { this.id = id; }
}
