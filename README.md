# Biblioteca - Proyecto Desarrollo Fullstack

Proyecto academico desarrollado para Duoc UC, ramo Desarrollo Fullstack.

El sistema corresponde a una API REST para gestion de libros y prestamos de biblioteca. El proyecto conserva el monolito original y, ademas, incluye una migracion a dos microservicios independientes.

## Estructura del proyecto

```text
Biblioteca/
├── biblioteca-monolito/
├── libro-service/
├── prestamo-service/
├── docker-compose.yml
├── docker/mariadb/init/01-create-databases.sql
├── README_MICROSERVICIOS.md
├── PRUEBAS_IMPLEMENTACION.md
└── PRESENTACION.md
```

Servicios:

- `biblioteca-monolito`: aplicacion original, puerto `8083`, base `prestamolibros_db`.
- `libro-service`: microservicio de libros, puerto `8081`, base `biblioteca_libros_db`.
- `prestamo-service`: microservicio de prestamos, puerto `8082`, base `biblioteca_prestamos_db`.

## Tecnologias principales

- Java 21
- Spring Boot 3.5.x en microservicios
- Spring Boot 3.3.x en monolito
- Maven
- MariaDB
- Flyway
- Spring Data JPA
- Spring Security
- JWT
- Swagger / OpenAPI
- OpenFeign
- Spring HATEOAS
- JUnit 5
- Mockito
- Docker Compose para MariaDB local

## Arquitectura actual

El proyecto esta organizado en tres modulos principales:

- `biblioteca-monolito` mantiene la aplicacion original.
- `libro-service` contiene el dominio de libros.
- `prestamo-service` contiene el dominio de prestamos.

La comunicacion entre microservicios se realiza desde `prestamo-service` hacia `libro-service` mediante OpenFeign.

`Prestamo` no tiene relacion JPA directa con `Libro`. En su lugar usa:

```java
private Long libroId;
```

Esto evita acoplamiento entre bases de datos y mantiene la separacion propia de microservicios.

## Bases de datos

Bases usadas por los microservicios:

- `biblioteca_libros_db`
- `biblioteca_prestamos_db`

Base usada por el monolito:

- `prestamolibros_db`

MariaDB corre en el puerto:

```text
3306
```

Credenciales de desarrollo para microservicios:

```text
Usuario: biblioteca_user
Password: biblioteca_pass
Root password Docker: rootpass
```

## Funcionalidades principales

`libro-service`:

- Crear libros
- Listar libros
- Buscar libro por ID
- Actualizar libros
- Eliminar libros
- Respuestas HATEOAS en endpoints `GET`

`prestamo-service`:

- Crear prestamos
- Listar prestamos
- Buscar prestamo por ID
- Actualizar prestamos
- Eliminar prestamos
- Validar existencia de libro usando OpenFeign
- Respuestas HATEOAS en endpoints `GET`

## Endpoints principales

`libro-service`

```text
GET    /api/v1/libros
GET    /api/v1/libros/{id}
POST   /api/v1/libros
PUT    /api/v1/libros/{id}
DELETE /api/v1/libros/{id}
POST   /auth/login
```

`prestamo-service`

```text
GET    /api/v1/prestamos
GET    /api/v1/prestamos/{id}
POST   /api/v1/prestamos
PUT    /api/v1/prestamos/{id}
DELETE /api/v1/prestamos/{id}
POST   /auth/login
```

## Swagger / OpenAPI

Swagger esta disponible en:

```text
http://localhost:8081/swagger-ui.html
http://localhost:8082/swagger-ui.html
http://localhost:8083/swagger-ui.html
```

Los microservicios documentan endpoints, DTOs, codigos de respuesta y autenticacion Bearer JWT.

Nota: los endpoints `GET` con HATEOAS devuelven `_links` y, en colecciones, `_embedded`. Algunos esquemas Swagger siguen mostrando el DTO base, por lo que la documentacion podria mejorarse si se quiere representar HATEOAS con mas precision.

## JWT

Credenciales de desarrollo:

```text
Usuario: admin
Password: admin123
```

Login en `libro-service`:

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Login en `prestamo-service`:

```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Reglas de seguridad:

- `GET /api/v1/**` es publico.
- `POST`, `PUT` y `DELETE` requieren `Authorization: Bearer <token>`.

## HATEOAS

HATEOAS basico esta implementado en endpoints `GET`.

Clases principales:

- `LibroModelAssembler`
- `PrestamoModelAssembler`

Tipos usados:

- `EntityModel`
- `CollectionModel`
- `RepresentationModelAssembler`

Links actuales:

- `self`
- `todos-los-libros`
- `todos-los-prestamos`

Ejemplo simplificado:

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

## Pruebas automaticas

El proyecto ya incorpora pruebas unitarias iniciales con JUnit 5 y Mockito en los microservicios.

Archivos actuales:

- `libro-service/src/test/java/com/duoc/libros/service/LibroServiceTest.java`
- `prestamo-service/src/test/java/com/duoc/prestamos/service/PrestamoServiceTest.java`

Pruebas actuales en `LibroServiceTest`:

- Guardar libro correctamente.
- Lanzar `ResourceNotFoundException` al buscar libro inexistente.
- Lanzar `ResourceNotFoundException` al eliminar libro inexistente.

Pruebas actuales en `PrestamoServiceTest`:

- Guardar prestamo correctamente cuando el libro existe.
- Lanzar `BadRequestException` cuando la fecha de termino es menor a la fecha de inicio.

Pendiente de completar:

- Completar la prueba de prestamo con libro inexistente.
- Agregar pruebas de controllers con MockMvc si la pauta lo exige.
- Agregar mas pruebas de actualizacion, eliminacion y excepciones.

Ejecutar pruebas de todo el proyecto:

```bash
./mvnw test
```

Ejecutar pruebas por microservicio:

```bash
cd libro-service
./mvnw test
```

```bash
cd prestamo-service
./mvnw test
```

Pruebas recomendadas para presentar al profesor:

1. `LibroServiceTest.guardarLibroCorrectamente`
2. `PrestamoServiceTest.guardarPrestamoCorrectamenteCuandoLibroExiste`
3. `PrestamoServiceTest.guardarPrestamoConFechaTerminoMenorAFechaInicioLanzaExcepcion`

Cuando se complete el caso pendiente, tambien es una muy buena opcion presentar:

```text
PrestamoServiceTest.guardarPrestamoConLibroInexistenteLanzaExcepcion
```

## Ejecutar en macOS con Docker Compose

Requisitos:

- JDK 21
- Docker Desktop
- Maven Wrapper incluido en el proyecto

Desde la raiz del proyecto:

```bash
docker compose up -d
```

Esto levanta MariaDB y crea:

- `biblioteca_libros_db`
- `biblioteca_prestamos_db`

Compilar:

```bash
./mvnw clean package -DskipTests
```

Levantar `libro-service`:

```bash
cd libro-service
./mvnw spring-boot:run
```

Levantar `prestamo-service` en otra terminal:

```bash
cd prestamo-service
./mvnw spring-boot:run
```

Orden recomendado:

1. Levantar MariaDB con Docker Compose.
2. Levantar `libro-service`.
3. Levantar `prestamo-service`.

Verificar MariaDB:

```bash
docker compose ps
docker exec -it biblioteca-mariadb mariadb -ubiblioteca_user -pbiblioteca_pass -e "SHOW DATABASES;"
```

Detener MariaDB:

```bash
docker compose down
```

Detener MariaDB y borrar volumen:

```bash
docker compose down -v
```

## Ejecutar en Windows con Laragon

Laragon incluye MariaDB/MySQL y permite levantar la base local sin Docker.

Requisitos:

- JDK 21
- Laragon instalado
- Maven Wrapper incluido en el proyecto
- Puerto `3306` disponible

Pasos:

1. Abrir Laragon.
2. Iniciar el servicio de base de datos.
3. Abrir HeidiSQL, phpMyAdmin o la terminal MariaDB de Laragon.
4. Crear las bases y usuario de los microservicios.

SQL recomendado para Laragon:

```sql
CREATE DATABASE IF NOT EXISTS biblioteca_libros_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS biblioteca_prestamos_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS prestamolibros_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'biblioteca_user'@'localhost'
    IDENTIFIED BY 'biblioteca_pass';

GRANT ALL PRIVILEGES ON biblioteca_libros_db.* TO 'biblioteca_user'@'localhost';
GRANT ALL PRIVILEGES ON biblioteca_prestamos_db.* TO 'biblioteca_user'@'localhost';

FLUSH PRIVILEGES;
```

Si Laragon no permite crear usuario con esa instruccion, usar el usuario `root` de Laragon y ajustar temporalmente en los `application.properties` de los microservicios:

```properties
spring.datasource.username=root
spring.datasource.password=
```

Luego ejecutar en Windows:

```bat
mvnw.cmd clean package -DskipTests
```

Levantar `libro-service`:

```bat
cd libro-service
..\mvnw.cmd spring-boot:run
```

Levantar `prestamo-service` en otra terminal:

```bat
cd prestamo-service
..\mvnw.cmd spring-boot:run
```

## Ejecutar el monolito

El monolito se mantiene disponible como referencia del proyecto original.

```bash
cd biblioteca-monolito
./mvnw spring-boot:run
```

En Windows:

```bat
cd biblioteca-monolito
..\mvnw.cmd spring-boot:run
```

URL:

```text
http://localhost:8083
```

## Validaciones rapidas

Ver Swagger:

```text
http://localhost:8081/swagger-ui.html
http://localhost:8082/swagger-ui.html
```

Probar HATEOAS:

```bash
curl http://localhost:8081/api/v1/libros
curl http://localhost:8082/api/v1/prestamos
```

En las respuestas deben aparecer:

```text
_links
```

Compilar y ejecutar pruebas:

```bash
./mvnw clean test
```

## Documentacion adicional

- `README_MICROSERVICIOS.md`: guia tecnica de ejecucion y estado de microservicios.
- `PRUEBAS_IMPLEMENTACION.md`: guia de pruebas con JUnit 5, Mockito y MockMvc.
- `PRESENTACION.md`: apoyo para exposicion oral individual.

## Estado academico actual

Completado:

- Migracion desde monolito hacia microservicios.
- CRUD de libros.
- CRUD de prestamos.
- Bases separadas.
- Flyway.
- JWT.
- Swagger/OpenAPI.
- OpenFeign.
- HATEOAS basico.
- Pruebas unitarias iniciales con JUnit 5 y Mockito.

Pendiente o mejorable:

- Completar la prueba de prestamo con libro inexistente.
- Agregar MockMvc si se exige probar controllers.
- Mejorar Swagger para representar con mayor detalle respuestas HATEOAS.
- Dockerizar microservicios solo si se requiere una etapa posterior.
