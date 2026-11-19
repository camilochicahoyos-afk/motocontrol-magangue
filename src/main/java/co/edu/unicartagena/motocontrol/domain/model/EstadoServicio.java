package co.edu.unicartagena.motocontrol.domain.model;

public enum EstadoServicio {
    EN_CURSO("en curso"),
    FINALIZADO("finalizada"),
    CANCELADO("cancelada");

    private final String descripcion;

    EstadoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    /** Texto para el usuario final (por ejemplo "finalizada"). */
    public String getDescripcion() {
        return descripcion;
    }
}
