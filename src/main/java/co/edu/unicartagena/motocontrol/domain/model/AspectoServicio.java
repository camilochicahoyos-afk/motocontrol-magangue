package co.edu.unicartagena.motocontrol.domain.model;

/**
 * Opciones rapidas que el pasajero puede marcar al calificar una carrera: lo bueno y lo que el
 * mototaxista debe mejorar.
 */
public enum AspectoServicio {

    CONDUCE_CON_PRECAUCION("Conduce con precaución", true),
    PUNTUAL("Llegó rápido / fue puntual", true),
    AMABLE("Amable y respetuoso", true),
    OFRECIO_CASCO("Ofreció casco al pasajero", true),
    MOTO_EN_BUEN_ESTADO("Moto limpia y en buen estado", true),
    COBRO_LO_ACORDADO("Cobró lo acordado", true),
    CONOCE_LOS_BARRIOS("Conoce bien los barrios", true),

    MUY_RAPIDO("Manejó muy rápido", false),
    MUY_LENTO("Manejó muy lento", false),
    NO_OFRECIO_CASCO("No ofreció casco", false),
    USO_CELULAR("Usó el celular mientras manejaba", false),
    MANIOBRAS_PELIGROSAS("Maniobras peligrosas o se pasó semáforos", false),
    TRATO_IRRESPETUOSO("Trato irrespetuoso", false),
    COBRO_DE_MAS("Cobró más de lo acordado", false),
    MOTO_EN_MAL_ESTADO("Moto en mal estado", false),
    VUELTAS_INNECESARIAS("Dio vueltas innecesarias", false),
    SE_DEMORO("Se demoró en llegar", false);

    private final String descripcion;
    private final boolean positivo;

    AspectoServicio(String descripcion, boolean positivo) {
        this.descripcion = descripcion;
        this.positivo = positivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isPositivo() {
        return positivo;
    }
}
