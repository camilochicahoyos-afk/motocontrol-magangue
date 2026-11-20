# MotoControl Magangué

Servicio web **API REST** para el registro y control del mototaxismo en Magangué (Bolívar): mototaxistas, motocicletas (con control de SOAT y revisión técnico-mecánica), carreras con resumen diario y **calificación de los mototaxistas** por parte de los pasajeros. Incluye una **página web en español** para el usuario final, sin términos técnicos.

Trabajo Colaborativo Contextualizado de **Ingeniería de Software** — Universidad de Cartagena, Centro Tutorial Magangué. CIPA *Chatgpteros*: Diego José Díaz Gómez, Camilo José Chica Hoyos, Yordana Pabuena Acosta y Mario José Monroy Durán.

| Tecnología | Versión |
|---|---|
| Java | 17 o superior |
| Spring Boot | 3.5.7 (Web, Data JPA, Validation, Actuator) |
| Base de datos | PostgreSQL 16 (H2 en memoria con el perfil `local`) |
| Documentación técnica | springdoc-openapi (Swagger UI) |
| Pruebas | JUnit 5, Mockito, AssertJ, MockMvc (36 pruebas) y Postman (30 peticiones) |
| Contenedores | Docker y Docker Compose |

## Versión publicada

La página está publicada en **https://motocontrol-magangue.onrender.com** (plan gratuito de Render). Si nadie la usa durante 15 minutos se apaga, y la primera visita después puede tardar unos 50 segundos o más en abrir. Esa versión arranca con datos de prueba (perfil `demo`) y usa una base de datos en memoria, así que lo que se registre allí se borra cuando el servicio se reinicia.

## Cómo se usa

| Dirección | Para quién |
|---|---|
| http://localhost:8080 | Página en español: Resumen del día, Mototaxistas, Motocicletas, Carreras, Calificaciones y ¿Cómo funciona? |
| http://localhost:8080/swagger-ui.html | Documentación técnica de la API (desarrolladores) |
| http://localhost:8080/actuator/health | Estado del servicio |

### Con Docker (API + PostgreSQL)

```bash
docker compose up --build
```

Para detener: `docker compose down` (agregue `-v` para borrar los datos). En Windows también puede dar doble clic a `3_levantar_docker.bat`.

### Con Apache NetBeans (sin Docker)

1. *File → Open Project* y elegir la carpeta `motocontrol-magangue` (la que tiene `pom.xml`).
2. **Run** (F6): el archivo `nbactions.xml` activa el perfil `local`, que usa H2 en memoria (no necesita PostgreSQL). Los datos se borran al detener la aplicación.
3. **Test** (Alt+F6): ejecuta las 36 pruebas JUnit.

Con Maven por consola: `mvn spring-boot:run -Dspring-boot.run.profiles=local` y `mvn test` (o `2_ejecutar_pruebas.bat`).

Para arrancar con los mismos datos de prueba de la versión publicada, use los perfiles `local,demo` (por ejemplo, `mvn spring-boot:run -Dspring-boot.run.profiles=local,demo`).

## Endpoints (23)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/conductores` | Registrar mototaxista |
| GET | `/api/v1/conductores?estado=ACTIVO` | Listar (filtro opcional) |
| GET | `/api/v1/conductores/{id}` | Consultar por id |
| GET | `/api/v1/conductores/documento/{doc}` | Consultar por documento |
| PUT | `/api/v1/conductores/{id}` | Actualizar datos |
| PATCH | `/api/v1/conductores/{id}/estado` | Cambiar estado (ACTIVO, SUSPENDIDO, INACTIVO) |
| DELETE | `/api/v1/conductores/{id}` | Dar de baja (borrado lógico) |
| POST | `/api/v1/motocicletas` | Registrar motocicleta |
| GET | `/api/v1/motocicletas?conductorId=1` | Listar |
| GET | `/api/v1/motocicletas/{id}` | Consultar por id |
| GET | `/api/v1/motocicletas/placa/{placa}` | Consultar por placa |
| PATCH | `/api/v1/motocicletas/{id}/documentos` | Actualizar SOAT y técnico-mecánica |
| DELETE | `/api/v1/motocicletas/{id}` | Desactivar |
| POST | `/api/v1/servicios` | Iniciar carrera (reglas RN-04 a RN-09) |
| GET | `/api/v1/servicios/{id}` | Consultar carrera |
| GET | `/api/v1/servicios?conductorId=1` | Carreras de un mototaxista |
| PATCH | `/api/v1/servicios/{id}/finalizar` | Finalizar |
| PATCH | `/api/v1/servicios/{id}/cancelar` | Cancelar |
| GET | `/api/v1/servicios/resumen?fecha=2026-11-14` | Resumen del día |
| POST | `/api/v1/calificaciones` | Calificar una carrera finalizada (1 a 5 estrellas) |
| GET | `/api/v1/calificaciones/opciones` | Escala de estrellas y opciones rápidas |
| GET | `/api/v1/calificaciones/conductores/{id}` | Reputación y opiniones de un mototaxista |
| GET | `/api/v1/calificaciones/ranking` | Ranking de mototaxistas |

### Errores con solución

Todos los errores responden con el mismo formato. Ejemplo (422):

```json
{
  "status": 422,
  "error": "Regla de negocio incumplida",
  "mensaje": "El SOAT de la motocicleta XYZ45E venció el 01/11/2026.",
  "solucion": "Renueve el SOAT y actualice la fecha en la sección de motocicletas.",
  "ruta": "/api/v1/servicios",
  "detalles": []
}
```

Códigos: 200, 201, 204, 400 (datos inválidos, cada campo explicado en español), 404 (no existe), 409 (dato repetido), 422 (regla de negocio) y 500 (error inesperado).

## Reglas de negocio

| Código | Regla |
|---|---|
| RN-01 | Documento, licencia y placa son únicos |
| RN-02 | Máximo dos motocicletas activas por mototaxista |
| RN-03 | Nada se borra: los mototaxistas se dan de baja y las motos se desactivan |
| RN-04 | Solo los mototaxistas activos hacen carreras |
| RN-05 | Licencia de conducción vigente |
| RN-06 | La moto debe estar activa y a nombre del mototaxista |
| RN-07 | SOAT y técnico-mecánica vigentes |
| RN-08 | Tarifa entre $2.000 y $15.000 (configurable en `application.yml`) |
| RN-09 | Una sola carrera en curso por mototaxista |
| RN-10 | Solo las carreras en curso se finalizan o cancelan |
| RN-11 | Solo se califican carreras finalizadas |
| RN-12 | Cada carrera se califica una sola vez |
| RN-13 | Con 3 o más calificaciones y promedio menor a 3, el mototaxista queda «para revisión» |

Las reglas RN-04 a RN-09 son clases independientes que implementan `ReglaServicio` (principio abierto/cerrado).

## Pruebas en Postman

Importe `postman/MotoControl-Magangue.postman_collection.json` y ejecútela completa con **Run collection** sobre una base de datos vacía. Tiene 30 peticiones en cuatro carpetas, cada una con sus pruebas automáticas (77 en total).

## Repositorio

https://github.com/camilochicahoyos-afk/motocontrol-magangue

```bash
git clone https://github.com/camilochicahoyos-afk/motocontrol-magangue.git
cd motocontrol-magangue
docker compose up --build
```

## Estructura

```
src/main/java/co/edu/unicartagena/motocontrol
├── config/             Reloj inyectable (America/Bogota), datos de Swagger y datos de prueba (perfil demo)
├── controller/         Controladores REST (JSON)
├── domain/model/       Entidades: Conductor, Motocicleta, Servicio, Calificacion
├── domain/repository/  Repositorios Spring Data JPA
├── dto/                Peticiones y respuestas (records)
├── exception/          Excepciones con solución y manejador global de errores
├── mapper/             Conversión entidad <-> DTO
├── service/            Interfaces, implementaciones y reglas de negocio
└── util/               Formato colombiano de fechas y valores
src/main/resources/static/index.html   Página web en español para el usuario final
docs/diagramas/                        Diagramas UML, entidad-relación y Scrum
postman/                               Colección de pruebas de Postman
```
