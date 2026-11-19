package co.edu.unicartagena.motocontrol.exception;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Traduce todas las excepciones a un mismo JSON con el codigo HTTP, que paso (mensaje) y como
 * corregirlo (solucion). Principio de responsabilidad unica: los controladores no manejan errores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Nombre en espanol de cada campo, en el mismo orden en que aparecen en los formularios. */
    private static final Map<String, String> CAMPOS = new LinkedHashMap<>();

    static {
        CAMPOS.put("numeroDocumento", "Número de documento");
        CAMPOS.put("nombres", "Nombres");
        CAMPOS.put("apellidos", "Apellidos");
        CAMPOS.put("telefono", "Teléfono celular");
        CAMPOS.put("barrio", "Barrio");
        CAMPOS.put("numeroLicencia", "Número de licencia");
        CAMPOS.put("vencimientoLicencia", "Vencimiento de la licencia");
        CAMPOS.put("placa", "Placa");
        CAMPOS.put("marca", "Marca");
        CAMPOS.put("linea", "Línea");
        CAMPOS.put("modelo", "Modelo (año)");
        CAMPOS.put("cilindraje", "Cilindraje");
        CAMPOS.put("vencimientoSoat", "Vencimiento del SOAT");
        CAMPOS.put("vencimientoTecnomecanica", "Vencimiento de la técnico-mecánica");
        CAMPOS.put("conductorId", "Mototaxista");
        CAMPOS.put("motocicletaId", "Motocicleta");
        CAMPOS.put("barrioOrigen", "Barrio de origen");
        CAMPOS.put("barrioDestino", "Barrio de destino");
        CAMPOS.put("tarifa", "Tarifa");
        CAMPOS.put("estado", "Estado");
        CAMPOS.put("servicioId", "Carrera");
        CAMPOS.put("estrellas", "Estrellas");
        CAMPOS.put("aspectos", "Opciones marcadas");
        CAMPOS.put("comentario", "Comentario");
        CAMPOS.put("fecha", "Fecha");
        CAMPOS.put("id", "Número de registro");
    }

    private static final Set<String> OBLIGATORIOS = Set.of("NotBlank", "NotNull", "NotEmpty");

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex,
                                                            HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getSolucion(), request, List.of());
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarDuplicado(RecursoDuplicadoException ex,
                                                         HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), ex.getSolucion(), request, List.of());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarReglaNegocio(ReglaNegocioException ex,
                                                            HttpServletRequest request) {
        return construir(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), ex.getSolucion(), request, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Algunos datos del formulario no son válidos.",
                "Corrija los campos indicados y vuelva a intentarlo.", request,
                detallarCampos(ex.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarFormato(HttpMessageNotReadableException ex,
                                                       HttpServletRequest request) {
        if (ex.getCause() instanceof InvalidFormatException invalido) {
            String campo = invalido.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(nombre -> nombre != null)
                    .reduce((primero, ultimo) -> ultimo)
                    .orElse("dato");
            return construir(HttpStatus.BAD_REQUEST,
                    "El dato «" + invalido.getValue() + "» no es válido para " + nombre(campo) + ".",
                    solucionPorTipo(invalido.getTargetType()), request, List.of());
        }
        return construir(HttpStatus.BAD_REQUEST, "Los datos enviados no tienen el formato correcto.",
                "Revise que las fechas tengan la forma 2026-11-14 y que los valores numéricos no lleven letras.",
                request, List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipo(MethodArgumentTypeMismatchException ex,
                                                    HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST,
                "El dato «" + ex.getValue() + "» no es válido para " + nombre(ex.getName()) + ".",
                solucionPorTipo(ex.getRequiredType()), request, List.of());
    }

    private static String solucionPorTipo(Class<?> tipo) {
        if (tipo != null && tipo.isEnum()) {
            return "Use uno de estos valores: " + Arrays.stream(tipo.getEnumConstants())
                    .map(valor -> ((Enum<?>) valor).name()).collect(Collectors.joining(", ")) + ".";
        }
        if (tipo == LocalDate.class) {
            return "Escriba la fecha como año-mes-día, por ejemplo 2026-11-14.";
        }
        return "Escriba solo números.";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> manejarParametroFaltante(MissingServletRequestParameterException ex,
                                                                 HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST,
                "Falta el dato " + nombre(ex.getParameterName()) + " (" + ex.getParameterName() + ").",
                "Indique ese dato y vuelva a intentarlo.", request, List.of());
    }

    /** Respaldo de RN-01 si dos personas registran el mismo dato al mismo tiempo. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex,
                                                          HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, "Ese dato ya está registrado en el sistema.",
                "Revise que el documento, la licencia, la placa o la carrera no estén repetidos.",
                request, List.of());
    }

    /** Cualquier otro error: rutas inexistentes, metodos no permitidos o fallas inesperadas. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarOtros(Exception ex, HttpServletRequest request) {
        if (ex instanceof org.springframework.web.ErrorResponse errorSpring) {
            HttpStatusCode codigo = errorSpring.getStatusCode();
            return switch (codigo.value()) {
                case 404 -> construir(HttpStatus.NOT_FOUND, "La dirección solicitada no existe.",
                        "Revise la dirección o vuelva a la página principal.", request, List.of());
                case 405 -> construir(HttpStatus.METHOD_NOT_ALLOWED,
                        "Esa operación no está disponible en esta dirección.",
                        "Consulte la documentación técnica para ver las operaciones permitidas.", request, List.of());
                case 415 -> construir(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "El formato de la petición no es compatible.",
                        "Envíe los datos en formato JSON.", request, List.of());
                default -> construir(HttpStatus.valueOf(codigo.value()), "La petición no se pudo procesar.",
                        "Revise los datos y vuelva a intentarlo.", request, List.of());
            };
        }
        LOG.error("Error inesperado en {}", request.getRequestURI(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor.",
                "Intente de nuevo. Si el problema continúa, avise al administrador del sistema.",
                request, List.of());
    }

    /** Un renglon por campo, con su nombre en espanol y en el orden del formulario. */
    private static List<String> detallarCampos(List<FieldError> errores) {
        Map<String, FieldError> porCampo = new LinkedHashMap<>();
        for (FieldError error : errores) {
            FieldError anterior = porCampo.get(error.getField());
            if (anterior == null || OBLIGATORIOS.contains(error.getCode())) {
                porCampo.put(error.getField(), error);
            }
        }
        List<String> campos = new ArrayList<>(porCampo.keySet());
        List<String> orden = new ArrayList<>(CAMPOS.keySet());
        campos.sort((a, b) -> Integer.compare(posicion(orden, a), posicion(orden, b)));
        return campos.stream()
                .map(campo -> CAMPOS.getOrDefault(campo, campo) + ": " + porCampo.get(campo).getDefaultMessage())
                .toList();
    }

    private static int posicion(List<String> orden, String campo) {
        int indice = orden.indexOf(campo);
        return indice < 0 ? Integer.MAX_VALUE : indice;
    }

    private static String nombre(String campo) {
        String nombre = CAMPOS.get(campo);
        return nombre == null ? "«" + campo + "»" : "«" + nombre + "»";
    }

    private static String titulo(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Datos inválidos";
            case NOT_FOUND -> "No encontrado";
            case CONFLICT -> "Dato repetido";
            case UNPROCESSABLE_ENTITY -> "Regla de negocio incumplida";
            case METHOD_NOT_ALLOWED -> "Operación no permitida";
            case UNSUPPORTED_MEDIA_TYPE -> "Formato no compatible";
            case INTERNAL_SERVER_ERROR -> "Error interno";
            default -> status.getReasonPhrase();
        };
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje, String solucion,
                                                    HttpServletRequest request, List<String> detalles) {
        ErrorResponse cuerpo = new ErrorResponse(LocalDateTime.now(), status.value(), titulo(status),
                mensaje, solucion, request.getRequestURI(), detalles);
        return ResponseEntity.status(status).body(cuerpo);
    }
}
