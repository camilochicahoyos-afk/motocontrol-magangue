package co.edu.unicartagena.motocontrol.domain.model;

public enum EstadoConductor {
    ACTIVO("activo"),
    SUSPENDIDO("suspendido"),
    INACTIVO("dado de baja");

    private final String descripcion;

    EstadoConductor(String descripcion) {
        this.descripcion = descripcion;
    }

    /** Texto para el usuario final (por ejemplo "suspendido"). */
    public String getDescripcion() {
        return descripcion;
    }
}
