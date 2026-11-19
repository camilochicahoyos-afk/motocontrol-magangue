package co.edu.unicartagena.motocontrol.service.regla;

/**
 * Contrato de una regla de negocio que se evalua antes de registrar un servicio.
 *
 * <p>Aplicacion del principio Abierto/Cerrado (OCP): para agregar una regla nueva basta con crear
 * otra clase que implemente esta interfaz y anotarla con {@code @Component}; el servicio que las
 * ejecuta no se modifica.</p>
 */
public interface ReglaServicio {

    /**
     * @throws co.edu.unicartagena.motocontrol.exception.ReglaNegocioException si la regla no se cumple
     */
    void validar(ContextoServicio contexto);
}
