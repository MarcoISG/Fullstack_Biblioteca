# Implementación de HATEOAS en API Biblioteca

## Objetivo

Agregar HATEOAS a la API para que las respuestas no devuelvan solo datos, sino también enlaces que permitan navegar entre recursos relacionados.

La idea es que cada recurso entregue links como:

- `self`
- colección
- actualización
- eliminación
- recurso relacionado

---

## Estado base del proyecto

Antes de aplicar HATEOAS, el proyecto ya tiene:

- Spring Boot
- CRUD de `Libro`
- CRUD de `Prestamo`
- DTOs de request y response
- Swagger
- Spring Security
- JWT
- JPA + Flyway + MariaDB

Esto permite aplicar HATEOAS directamente sobre la capa de respuesta sin rehacer la lógica de negocio.

---

## Paso 1: Agregar la dependencia

En `pom.xml` hay que agregar la dependencia de Spring HATEOAS.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

### Dónde ponerla

Dentro de `<dependencies>`, junto al resto de starters de Spring Boot.

### Qué aporta

Esta dependencia entrega clases como:

- `EntityModel`
- `CollectionModel`
- `RepresentationModel`
- `RepresentationModelAssembler`
- `WebMvcLinkBuilder`

---

## Paso 2: Definir la estrategia de respuesta

Aquí hay dos caminos válidos.

### Opción recomendada para este proyecto

Mantener los DTOs actuales y transformarlos en modelos HATEOAS.

Eso significa:

- `LibroResponseDTO` deja de ser solo un DTO plano
- `PrestamoResponseDTO` deja de ser solo un DTO plano
- ambos pasan a soportar links

### Forma más común

Hacer que cada DTO de respuesta extienda `RepresentationModel<T>`.

Ejemplo conceptual:

- `LibroResponseDTO extends RepresentationModel<LibroResponseDTO>`
- `PrestamoResponseDTO extends RepresentationModel<PrestamoResponseDTO>`

### Por qué conviene esta opción

Porque:

- ya tienes DTOs creados
- no obliga a cambiar entidades
- mantiene separada la capa REST del modelo JPA
- encaja bien con Swagger y con el estilo actual del proyecto

---

## Paso 3: Ajustar los DTOs de respuesta

Los DTOs que deben cambiar son:

- `LibroResponseDTO`
- `PrestamoResponseDTO`

### Qué cambio conceptual hay que hacer

Actualmente seguramente tienen campos como:

- `id`
- `titulo`
- `autor`
- `editorial`

o

- `id`
- `fechaInicio`
- `fechaTermino`
- `tituloLibro`

Ahora también deben poder contener `_links`.

### Importante

No debes meter lógica de armado de links dentro del DTO.

El DTO solo debe quedar preparado para recibir links.

---

## Paso 4: Crear los Assemblers

Este es el paso central de HATEOAS.

Hay que crear clases assembler para convertir DTOs planos en recursos enriquecidos con links.

### Clases a crear

- `LibroModelAssembler`
- `PrestamoModelAssembler`

### Ubicación sugerida

Crear un nuevo paquete, por ejemplo:

- `assembler`

Ruta sugerida:

- `src/main/java/com/universidad/biblioteca/assembler/`

### Interfaz a usar

Cada assembler debería implementar:

```java
RepresentationModelAssembler<LibroResponseDTO, EntityModel<LibroResponseDTO>>
```

y

```java
RepresentationModelAssembler<PrestamoResponseDTO, EntityModel<PrestamoResponseDTO>>
```

### Responsabilidad del assembler

Cada assembler debe:

1. recibir un DTO de respuesta
2. envolverlo en un `EntityModel`
3. agregar links

---

## Paso 5: Definir los links de cada recurso

## Para `Libro`

Cada libro debería incluir al menos:

- `self` -> `GET /api/v1/libros/{id}`
- `libros` -> `GET /api/v1/libros`

Opcionalmente también:

- `actualizar` -> `PUT /api/v1/libros/{id}`
- `eliminar` -> `DELETE /api/v1/libros/{id}`

### Recomendación práctica

Aunque los métodos `PUT` y `DELETE` requieran autenticación, igual puedes exponer los links como parte del recurso. Eso es válido en HATEOAS.

---

## Para `Prestamo`

Cada préstamo debería incluir al menos:

- `self` -> `GET /api/v1/prestamos/{id}`
- `prestamos` -> `GET /api/v1/prestamos`

Y además conviene agregar:

- `libro` -> `GET /api/v1/libros/{id del libro asociado}`

Opcionalmente:

- `actualizar` -> `PUT /api/v1/prestamos/{id}`
- `eliminar` -> `DELETE /api/v1/prestamos/{id}`

---

## Paso 6: Resolver un detalle importante en `PrestamoResponseDTO`

Aquí hay una decisión técnica importante.

Si `PrestamoResponseDTO` hoy solo expone el título del libro, por ejemplo:

- `tituloLibro`

entonces no alcanza para construir el link al libro relacionado.

### Lo correcto

El DTO de respuesta de préstamo debería incluir también:

- `libroId`

### Por qué

Porque para crear el link relacionado necesitas conocer el identificador real del libro.

### Entonces

`PrestamoResponseDTO` debería quedar con algo como:

- `id`
- `fechaInicio`
- `fechaTermino`
- `libroId`
- `tituloLibro`

---

## Paso 7: Modificar el mapeo en los services

Los métodos que convierten entidad a DTO deben actualizarse para soportar los nuevos campos necesarios.

Especialmente en `PrestamoService`:

- si hoy solo mapea el título del libro
- entonces debe empezar a mapear también el `id` del libro

### Ojo

Aquí no se agregan links todavía.

En el service solo se arma el DTO con datos.

Los links se agregan después, en el assembler.

---

## Paso 8: Modificar los endpoints `GET` individuales

Los controladores deben dejar de devolver solo DTOs planos.

### Antes

Algo como:

```java
ResponseEntity<LibroResponseDTO>
```

### Después

Debería pasar a devolver algo como:

```java
ResponseEntity<EntityModel<LibroResponseDTO>>
```

Lo mismo para `Prestamo`.

### Qué cambia dentro del método

En vez de retornar el DTO directamente:

1. el controller obtiene el DTO desde el service
2. se lo pasa al assembler
3. retorna el `EntityModel` con links

---

## Paso 9: Modificar los endpoints `GET` de listado

Los listados tampoco deberían devolver una lista plana simple.

### Antes

Algo como:

```java
ResponseEntity<List<LibroResponseDTO>>
```

### Después

Lo ideal es devolver:

```java
ResponseEntity<CollectionModel<EntityModel<LibroResponseDTO>>>
```

y equivalente para préstamos.

### Qué debe contener el listado

Cada elemento de la lista debe incluir sus links propios, y además la colección debería tener su propio link `self`.

---

## Paso 10: Aplicar HATEOAS a `POST`

Después de que los `GET` funcionen, se puede adaptar `POST`.

### Qué debería devolver

Cuando creas un libro o préstamo, la respuesta debería incluir:

- el recurso creado
- su link `self`
- idealmente status `201 Created`

### Mejora recomendable

Además del body con links, se puede devolver cabecera `Location` apuntando al `self` del recurso creado.

Esto queda muy alineado con REST bien hecho.

---

## Paso 11: Aplicar HATEOAS a `PUT`

Después del `POST`, adaptar `PUT`.

### Qué debería devolver

El recurso actualizado con:

- `self`
- colección
- enlaces relacionados

---

## Paso 12: Mantener `DELETE` simple

En `DELETE` no hace falta complicarse.

Se puede mantener:

- `204 No Content`

No es necesario devolver representación HATEOAS en eliminación.

---

## Paso 13: Revisar Swagger

Después de implementar HATEOAS, hay que probar en Swagger:

- `GET /api/v1/libros`
- `GET /api/v1/libros/{id}`
- `GET /api/v1/prestamos`
- `GET /api/v1/prestamos/{id}`

### Qué deberías ver

Ahora las respuestas incluirán estructura tipo:

- datos del recurso
- `_links`

Ejemplo conceptual:

```json
{
  "id": 1,
  "titulo": "Clean Code",
  "autor": "Robert C. Martin",
  "editorial": "Prentice Hall",
  "_links": {
    "self": {
      "href": "http://localhost:8083/api/v1/libros/1"
    },
    "libros": {
      "href": "http://localhost:8083/api/v1/libros"
    }
  }
}
```

---

## Paso 14: Orden exacto recomendado de implementación

Para no romper demasiadas cosas a la vez, conviene hacerlo en este orden:

1. agregar dependencia `spring-boot-starter-hateoas`
2. adaptar `LibroResponseDTO`
3. adaptar `PrestamoResponseDTO`
4. agregar `libroId` a `PrestamoResponseDTO` si aún no existe
5. ajustar mapeos en `PrestamoService`
6. crear `LibroModelAssembler`
7. crear `PrestamoModelAssembler`
8. modificar `GET /libros/{id}`
9. modificar `GET /prestamos/{id}`
10. modificar `GET /libros`
11. modificar `GET /prestamos`
12. probar en Swagger
13. adaptar `POST /libros`
14. adaptar `POST /prestamos`
15. adaptar `PUT /libros/{id}`
16. adaptar `PUT /prestamos/{id}`

---

## Estructura sugerida de paquetes

La estructura debería quedar algo así:

- `controller`
- `service`
- `repository`
- `dto`
- `model`
- `exception`
- `config`
- `assembler`

---

## Criterios para no hacerlo mal

### No mezclar responsabilidades

- Entity JPA: persistencia
- DTO: datos de entrada y salida
- Assembler: links HATEOAS
- Controller: orquestación HTTP
- Service: lógica de negocio

### No poner links en el service

El service no debería conocer detalles de navegación HTTP.

### No usar entidades JPA como respuesta REST

Sigue siendo mejor responder con DTOs.

### No empezar por todo a la vez

Primero `GET`, después `POST` y `PUT`.

---

## Resultado final esperado

Al terminar esta etapa, la API debería:

- seguir usando la misma lógica de negocio
- seguir usando JWT como hasta ahora
- devolver recursos navegables
- quedar más alineada con REST maduro
- quedar mejor preparada para futuras integraciones o separación en microservicios

---

## Resumen ejecutivo

La implementación correcta de HATEOAS en este proyecto consiste en:

- agregar la dependencia
- adaptar DTOs de salida
- crear assemblers
- cambiar responses de controladores
- empezar por `GET`
- luego extender a `POST` y `PUT`

La clave técnica es esta:

**los datos siguen saliendo desde los services, pero los links se agregan en assemblers antes de responder desde los controllers**
