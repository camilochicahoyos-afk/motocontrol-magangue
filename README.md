# MotoControl Magangué API

API REST para el **registro y control del servicio de mototaxismo en Magangué (Bolívar)**: mototaxistas, motocicletas (con control de SOAT y revisión técnico-mecánica) y servicios (carreras) con reporte diario.

Proyecto del Trabajo Colaborativo Contextualizado de **Ingeniería de Software** — Universidad de Cartagena, Centro Tutorial Magangué. CIPA *Chatgpteros*.

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.7 (Web, Data JPA, Validation, Actuator) |
| Base de datos | PostgreSQL 16 (H2 en memoria con el perfil `local`) |
| Documentación | springdoc-openapi (Swagger UI) |
| Pruebas | JUnit 5, Mockito, AssertJ, MockMvc |
| Contenedores | Docker y Docker Compose |

## Ejecutar con Docker (recomendado)

```bash
docker compose up --build
```

- API: http://localhost:8080/api/v1/conductores
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

Para detener: `docker compose down` (agregue `-v` para borrar los datos).

## Ejecutar sin Docker

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local   # usa H2 en memoria
mvn test                                               # pruebas unitarias JUnit
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/conductores` | Registrar mototaxista |
| GET | `/api/v1/conductores?estado=ACTIVO` | Listar (filtro opcional) |
| GET | `/api/v1/conductores/{id}` | Consultar por id |
| GET | `/api/v1/conductores/documento/{doc}` | Consultar por documento |
| PUT | `/api/v1/conductores/{id}` | Actualizar datos |
| PATCH | `/api/v1/conductores/{id}/estado` | Cambiar estado |
| DELETE | `/api/v1/conductores/{id}` | Inactivar (borrado lógico) |
| POST | `/api/v1/motocicletas` | Registrar motocicleta |
| GET | `/api/v1/motocicletas?conductorId=1` | Listar |
| GET | `/api/v1/motocicletas/{id}` | Consultar por id |
| GET | `/api/v1/motocicletas/placa/{placa}` | Consultar por placa |
| PATCH | `/api/v1/motocicletas/{id}/documentos` | Actualizar SOAT / tecnomecánica |
| DELETE | `/api/v1/motocicletas/{id}` | Desactivar |
| POST | `/api/v1/servicios` | Iniciar servicio |
| GET | `/api/v1/servicios/{id}` | Consultar servicio |
| GET | `/api/v1/servicios?conductorId=1` | Servicios de un conductor |
| PATCH | `/api/v1/servicios/{id}/finalizar` | Finalizar |
| PATCH | `/api/v1/servicios/{id}/cancelar` | Cancelar |
| GET | `/api/v1/servicios/resumen?fecha=2026-11-14` | Resumen diario |

Códigos de respuesta: 200, 201, 204, 400 (validación), 404 (no existe), 409 (duplicado), 422 (regla de negocio).

## Reglas de negocio

| Código | Regla |
|---|---|
| RN-01 | Documento, licencia y placa son únicos |
| RN-02 | Máximo dos motocicletas activas por conductor |
| RN-03 | Conductores y motos no se borran: se inactivan |
| RN-04 | Solo conductores ACTIVOS pueden prestar servicios |
| RN-05 | Licencia de conducción vigente |
| RN-06 | La moto debe estar activa y asignada al conductor |
| RN-07 | SOAT y técnico-mecánica vigentes |
| RN-08 | Tarifa entre $2.000 y $15.000 (configurable) |
| RN-09 | Un solo servicio en curso por conductor |

Las reglas RN-04 a RN-09 son clases independientes que implementan `ReglaServicio` (principio abierto/cerrado).

## Pruebas en Postman

Importe `postman/MotoControl-Magangue.postman_collection.json` y ejecútela completa con **Run collection** sobre una base de datos vacía. Cada petición trae sus pruebas automáticas.

## Repositorio

https://github.com/camilochicahoyos-afk/motocontrol-magangue

```bash
git clone https://github.com/camilochicahoyos-afk/motocontrol-magangue.git
cd motocontrol-magangue
docker compose up --build
```

En Windows también puede usar los scripts incluidos: `2_ejecutar_pruebas.bat` (pruebas JUnit con Maven) y `3_levantar_docker.bat` (API + PostgreSQL en Docker).

## Estructura

```
src/main/java/co/edu/unicartagena/motocontrol
├── config/        Reloj inyectable (zona America/Bogota)
├── controller/    Controladores REST (JSON)
├── domain/model/  Entidades JPA: Conductor, Motocicleta, Servicio
├── domain/repository/  Repositorios Spring Data
├── dto/           Peticiones y respuestas (records)
├── exception/     Excepciones y manejador global de errores
├── mapper/        Conversión entidad <-> DTO
└── service/       Interfaces, implementaciones y reglas de negocio
```
