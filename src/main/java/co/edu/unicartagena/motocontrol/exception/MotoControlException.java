package co.edu.unicartagena.motocontrol.exception;

/**
 * Base de los errores del sistema. Ademas del mensaje (que paso) lleva una solucion
 * (como corregirlo), para que el usuario final sepa que hacer.
 */
public abstract class MotoControlException extends RuntimeException {

    private final String solucion;

    protected MotoControlException(String mensaje, String solucion) {
        super(mensaje);
        this.solucion = solucion;
    }

    public String getSolucion() {
        return solucion;
    }
}
