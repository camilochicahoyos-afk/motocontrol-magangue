package co.edu.unicartagena.motocontrol.exception;

/**
 * Se lanza cuando el recurso solicitado no existe (HTTP 404).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " con identificador " + id + " no existe");
    }
}
