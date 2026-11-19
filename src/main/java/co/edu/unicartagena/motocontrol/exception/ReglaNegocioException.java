package co.edu.unicartagena.motocontrol.exception;

/**
 * Se lanza cuando una operacion incumple una regla de negocio (HTTP 422).
 */
public class ReglaNegocioException extends MotoControlException {

    public ReglaNegocioException(String mensaje, String solucion) {
        super(mensaje, solucion);
    }
}
