# Estado Actual y Siguiente Paso: HATEOAS

## En qué quedamos

El proyecto quedó limpio y enfocado en la aplicación actual de `biblioteca`.

### Ya resuelto

- Se eliminaron rastros de entorno y archivos que no aportaban al desarrollo actual.
- La base activa del proyecto es `com.universidad.biblioteca`.
- El proyecto compila correctamente con Maven.
- Ya existen CRUD completos para:
  - `Libro`
  - `Prestamo`
- Ya están integrados:
  - Spring Web
  - Spring Data JPA
  - Flyway
  - Swagger / OpenAPI
  - Spring Security
  - JWT

### Qué no hicimos todavía

- No se aplicó HATEOAS aún.
- No se agregaron pruebas automatizadas.
- No se modificó la estructura actual de respuestas REST.

## Decisión tomada

El siguiente paso será avanzar directo con **HATEOAS**.

La razón es simple:

- la base REST ya existe
- los controladores ya están funcionando
- los DTOs ya están definidos
- ya tiene sentido enriquecer las respuestas con enlaces

## Qué significa aplicar HATEOAS aquí

La API dejará de devolver solo datos planos y comenzará a devolver también enlaces útiles para navegar la API.

Ejemplo conceptual:

- un libro podrá incluir enlace a:
  - sí mismo
  - listado de libros
  - actualización
  - eliminación
- un préstamo podrá incluir enlace a:
  - sí mismo
  - listado de préstamos
  - libro asociado

## Orden recomendado para implementarlo

### Paso 1

Agregar la dependencia de Spring HATEOAS en `pom.xml`.

### Paso 2

Adaptar los modelos de respuesta para soportar enlaces.

### Paso 3

Crear `Assembler` o `RepresentationModelAssembler` para:

- `Libro`
- `Prestamo`

### Paso 4

Modificar los controladores para devolver recursos con links:

- `GET /api/v1/libros`
- `GET /api/v1/libros/{id}`
- `GET /api/v1/prestamos`
- `GET /api/v1/prestamos/{id}`

### Paso 5

Extender HATEOAS a respuestas de:

- `POST`
- `PUT`

### Paso 6

Validar el resultado en Swagger.

## Criterio práctico para hacerlo bien

Conviene empezar por los `GET`, porque ahí HATEOAS entrega más valor inmediatamente y es más fácil dejar una convención consistente antes de tocar creación y actualización.

## Resultado esperado

Cuando implementemos esta etapa, la API debería:

- seguir funcionando igual a nivel de negocio
- mantener seguridad y JWT como están
- devolver respuestas enriquecidas con links
- quedar mejor preparada para una evolución posterior a microservicios o integración entre servicios

## Resumen corto

Quedamos con el proyecto limpio, compilando y listo para seguir.

La siguiente tarea acordada es:

**implementar HATEOAS primero, sin cambiar todavía la lógica de negocio**
