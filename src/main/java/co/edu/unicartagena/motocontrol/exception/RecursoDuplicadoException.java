package co.edu.unicartagena.motocontrol.exception;

/**
 * Se lanza cuando se intenta registrar un dato que debe ser unico (HTTP 409).
 */
public class RecursoDuplicadoException extends MotoControlException {

    public RecursoDuplicadoException(String mensaje, String solucion) {
        super(mensaje, solucion);
    }
}
