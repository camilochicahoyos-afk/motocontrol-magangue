package co.edu.unicartagena.motocontrol.exception;

/**
 * Se lanza cuando el recurso solicitado no existe (HTTP 404).
 */
public class RecursoNoEncontradoException extends MotoControlException {

    public RecursoNoEncontradoException(String mensaje, String solucion) {
        super(mensaje, solucion);
    }

    public static RecursoNoEncontradoException mototaxista(Object id) {
        return new RecursoNoEncontradoException("No existe un mototaxista con el número " + id + ".",
                "Revise el dato o búsquelo en la lista de mototaxistas.");
    }

    public static RecursoNoEncontradoException motocicleta(Object id) {
        return new RecursoNoEncontradoException("No existe una motocicleta con el número " + id + ".",
                "Revise el dato o búsquela en la lista de motocicletas.");
    }

    public static RecursoNoEncontradoException carrera(Object id) {
        return new RecursoNoEncontradoException("No existe la carrera número " + id + ".",
                "Revise el número de la carrera en el historial del mototaxista.");
    }
}
