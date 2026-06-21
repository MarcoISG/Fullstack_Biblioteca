# Ayuda de presentacion individual - Proyecto Biblioteca

Este documento sirve como apoyo para la evaluacion oral individual. La informacion esta basada en el estado actual del proyecto `Biblioteca` y no agrega funcionalidades que no existan en el codigo.

## 1. Justificacion de herramientas de prueba utilizadas

Estado real del proyecto:

- Actualmente no existen pruebas automaticas implementadas en `libro-service` ni en `prestamo-service`.
- No hay archivos Java dentro de `src/test` en los microservicios.
- En los microservicios no se encontro dependencia activa de JUnit, Mockito ni MockMvc.
- El monolito conserva `spring-boot-starter-test` en su `pom.xml`, pero no hay pruebas automaticas desarrolladas.

Como presentarlo oralmente:

> En esta etapa del proyecto la validacion principal se realizo mediante compilacion Maven, ejecucion manual de los servicios, Swagger, Postman o curl, y verificacion de respuestas HTTP. Las pruebas automaticas con JUnit 5, Mockito y MockMvc quedan como pendiente principal para cerrar la pauta academica si el profesor las exige.

Herramientas recomendadas como pendiente:

- JUnit 5: para validar reglas de negocio en servicios.
- Mockito: para simular repositorios y clientes externos como OpenFeign.
- MockMvc: para validar controllers sin levantar todo el servidor, solo si la pauta lo exige.

## 2. Explicacion de pruebas realizadas o pendientes

Pruebas manuales realizadas y alineadas al proyecto:

- Compilacion de los tres modulos Maven.
- Inicio de `libro-service` en puerto `8081`.
- Inicio de `prestamo-service` en puerto `8082`.
- Validacion de Swagger en ambos microservicios.
- Login JWT en ambos microservicios.
- CRUD completo de libros.
- CRUD completo de prestamos.
- Validacion de OpenFeign desde `prestamo-service` hacia `libro-service`.
- Validacion de error controlado cuando se intenta crear un prestamo con un `libroId` inexistente.
- Validacion de respuestas HATEOAS en endpoints `GET`.

Endpoints validados en `libro-service`:

- `GET /api/v1/libros`
- `GET /api/v1/libros/{id}`
- `POST /api/v1/libros`
- `PUT /api/v1/libros/{id}`
- `DELETE /api/v1/libros/{id}`

Endpoints validados en `prestamo-service`:

- `GET /api/v1/prestamos`
- `GET /api/v1/prestamos/{id}`
- `POST /api/v1/prestamos`
- `PUT /api/v1/prestamos/{id}`
- `DELETE /api/v1/prestamos/{id}`

Pendiente:

- Crear pruebas unitarias con JUnit 5.
- Crear pruebas con Mockito para servicios.
- Crear pruebas de controllers con MockMvc si corresponde.
- Probar validaciones de DTOs.
- Probar excepciones controladas.
- Probar `PrestamoService` mockeando `LibroClient`.

## 3. Descripcion de Swagger y OpenAPI en el proyecto

El proyecto usa Swagger/OpenAPI mediante `springdoc-openapi-starter-webmvc-ui`.

Servicios con Swagger:

- `libro-service`: `http://localhost:8081/swagger-ui.html`
- `prestamo-service`: `http://localhost:8082/swagger-ui.html`
- `biblioteca-monolito`: `http://localhost:8083/swagger-ui.html`

Archivos relevantes:

- `libro-service/src/main/java/com/duoc/libros/config/SwaggerConfig.java`
- `prestamo-service/src/main/java/com/duoc/prestamos/config/SwaggerConfig.java`
- `biblioteca-monolito/src/main/java/com/universidad/biblioteca/config/SwaggerConfig.java`

Que permite explicar:

- Swagger permite visualizar los endpoints REST disponibles.
- OpenAPI documenta contratos de entrada y salida.
- Los controllers usan anotaciones como `@Operation`, `@ApiResponse`, `@Schema` y `@Tag`.
- Tambien se documento seguridad Bearer JWT en la configuracion OpenAPI.

Observacion honesta:

- Swagger funciona, pero en los endpoints `GET` con HATEOAS la respuesta real incluye `_links` y, en colecciones, `_embedded`.
- Algunos annotations siguen mostrando el DTO base como esquema principal. Esto no rompe el funcionamiento, pero se puede mejorar para documentar con mas precision las respuestas HATEOAS.

## 4. Implementacion y ejemplo de respuesta con HATEOAS

Estado real:

- HATEOAS basico esta implementado en `libro-service` y `prestamo-service`.
- Se usa `spring-boot-starter-hateoas`.
- Se usan `EntityModel`, `CollectionModel` y `RepresentationModelAssembler`.
- Existen assemblers propios:
  - `LibroModelAssembler`
  - `PrestamoModelAssembler`

Archivos relevantes:

- `libro-service/src/main/java/com/duoc/libros/assembler/LibroModelAssembler.java`
- `prestamo-service/src/main/java/com/duoc/prestamos/assembler/PrestamoModelAssembler.java`
- `libro-service/src/main/java/com/duoc/libros/controller/LibroController.java`
- `prestamo-service/src/main/java/com/duoc/prestamos/controller/PrestamoController.java`

Endpoints con HATEOAS:

- `GET /api/v1/libros`
- `GET /api/v1/libros/{id}`
- `GET /api/v1/prestamos`
- `GET /api/v1/prestamos/{id}`

Links implementados en libros:

- `self`
- `todos-los-libros`

Links implementados en prestamos:

- `self`
- `todos-los-prestamos`

Ejemplo de respuesta esperada para un libro individual:

```json
{
  "id": 1,
  "titulo": "Clean Code",
  "autor": "Robert C. Martin",
  "editorial": "Prentice Hall",
  "_links": {
    "self": {
      "href": "http://localhost:8081/api/v1/libros/1"
    },
    "todos-los-libros": {
      "href": "http://localhost:8081/api/v1/libros"
    }
  }
}
```

Ejemplo de respuesta esperada para un prestamo individual:

```json
{
  "id": 1,
  "fechaInicio": "2027-01-10",
  "fechaTermino": "2027-01-20",
  "libroId": 1,
  "tituloLibro": "Clean Code",
  "_links": {
    "self": {
      "href": "http://localhost:8082/api/v1/prestamos/1"
    },
    "todos-los-prestamos": {
      "href": "http://localhost:8082/api/v1/prestamos"
    }
  }
}
```

Observacion honesta:

- `POST` y `PUT` devuelven DTO plano, no `EntityModel`.
- Esto no rompe el CRUD ni el HATEOAS basico en `GET`.
- Si el profesor exige HATEOAS tambien en creacion o actualizacion, quedaria como mejora pendiente.

## 5. Caso hipotetico de despliegue en nube

Este punto es opcional y no esta implementado actualmente.

Estado real:

- Existe Docker Compose solo para MariaDB local.
- No existe Dockerizacion de los microservicios.
- No existe despliegue en nube.
- No existe API Gateway.
- No existe Eureka.
- No existe Kafka, RabbitMQ ni Circuit Breaker.

Como presentarlo sin inventar:

> Como caso hipotetico, el proyecto podria desplegarse en nube separando cada microservicio en su propio contenedor, usando una base MariaDB administrada o contenedores separados por ambiente. `libro-service` y `prestamo-service` se podrian publicar como servicios independientes. `prestamo-service` deberia configurar la URL de `libro-service` mediante variable de entorno en lugar de dejarla fija en `application.properties`.

Posibles pasos futuros:

- Crear `Dockerfile` para `libro-service`.
- Crear `Dockerfile` para `prestamo-service`.
- Externalizar variables como datasource, usuario, password, JWT secret y `libro-service.url`.
- Usar un servicio administrado de base de datos o contenedores separados.
- Agregar monitoreo y logs centralizados.
- Evaluar API Gateway o service discovery solo si la pauta futura lo solicita.

## 6. Reflexion personal sobre trabajo colaborativo y aprendizajes tecnicos

Esta seccion debe completarla cada integrante con su experiencia real.

Guia para responder:

- Que parte del proyecto entendiste mejor durante la migracion.
- Que dificultad tecnica aparecio al separar el monolito.
- Que aprendiste sobre bases separadas por microservicio.
- Que aprendiste sobre OpenFeign.
- Que aprendiste sobre JWT y Swagger.
- Que aprendiste al implementar HATEOAS.
- Que faltaria mejorar si hubiera mas tiempo.

Ejemplo editable, no obligatorio:

> Durante el proyecto aprendi que migrar un monolito a microservicios no es solo copiar clases. Fue necesario separar responsabilidades, eliminar relaciones JPA directas entre servicios y reemplazarlas por comunicacion HTTP mediante OpenFeign. Tambien aprendi la importancia de documentar la API con Swagger y de agregar HATEOAS para que las respuestas entreguen enlaces utiles al cliente. El principal pendiente tecnico es incorporar pruebas automaticas para respaldar el funcionamiento del CRUD, las validaciones, JWT y la comunicacion entre servicios.

## Resumen rapido para exposicion

Arquitectura actual:

- Monolito conservado en `biblioteca-monolito`.
- Microservicio de libros en `libro-service`.
- Microservicio de prestamos en `prestamo-service`.
- Bases separadas: `biblioteca_libros_db` y `biblioteca_prestamos_db`.
- Comunicacion entre microservicios mediante OpenFeign.
- Seguridad con JWT.
- Documentacion con Swagger/OpenAPI.
- HATEOAS basico en endpoints `GET`.

Pendiente principal:

- Pruebas automaticas con JUnit 5, Mockito y MockMvc si la pauta lo exige.

Frase de cierre sugerida:

> El proyecto se encuentra funcional a nivel de arquitectura, CRUD, JWT, Swagger, OpenFeign y HATEOAS basico. El cierre academico pendiente corresponde principalmente a incorporar pruebas automaticas para respaldar formalmente el comportamiento ya validado manualmente.
