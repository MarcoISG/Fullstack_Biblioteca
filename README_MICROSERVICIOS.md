# Proyecto Biblioteca - Estado y ejecucion

Este documento resume el estado actual del proyecto Biblioteca despues de la migracion desde monolito hacia microservicios.

## Estructura

```text
Biblioteca/
├── biblioteca-monolito/
├── libro-service/
├── prestamo-service/
├── docker-compose.yml
└── docker/mariadb/init/01-create-databases.sql
```

Servicios actuales:

- `biblioteca-monolito`: puerto `8083`, base `prestamolibros_db`.
- `libro-service`: puerto `8081`, base `biblioteca_libros_db`.
- `prestamo-service`: puerto `8082`, base `biblioteca_prestamos_db`.

## Estado actual

Completado:

- Dominio `Libro` migrado a `libro-service`.
- Dominio `Prestamo` migrado a `prestamo-service`.
- Bases separadas por microservicio.
- Flyway configurado en monolito y microservicios.
- CRUD de libros funcionando.
- CRUD de prestamos funcionando.
- Spring Security con JWT funcionando.
- Swagger/OpenAPI funcionando.
- OpenFeign funcionando desde `prestamo-service` hacia `libro-service`.
- `Prestamo` usa `libroId`, sin relacion JPA directa con `Libro`.
- HATEOAS basico implementado en respuestas `GET`.
- `LibroModelAssembler` implementado.
- `PrestamoModelAssembler` implementado.
- No hay API Gateway, Eureka, Kafka, RabbitMQ ni Circuit Breaker.

Pendiente principal:

- Implementar pruebas automaticas si la pauta academica lo exige.
- Pruebas sugeridas: JUnit 5, Mockito y MockMvc para controllers si corresponde.

## Requisitos

- JDK 21 configurado en el proyecto.
- Maven Wrapper incluido en la raiz.
- Docker Desktop o Docker Engine con Docker Compose.
- Puerto `3306` disponible para MariaDB.

## Levantar MariaDB con Docker

Desde la raiz del proyecto:

```bash
docker compose up -d
```

El contenedor levanta MariaDB en `localhost:3306`.

Credenciales de desarrollo:

```text
Usuario: biblioteca_user
Password: biblioteca_pass
Root password: rootpass
```

Bases creadas automaticamente:

- `biblioteca_libros_db`
- `biblioteca_prestamos_db`

Comprobar estado:

```bash
docker compose ps
docker logs biblioteca-mariadb
```

Ver bases:

```bash
docker exec -it biblioteca-mariadb mariadb -ubiblioteca_user -pbiblioteca_pass -e "SHOW DATABASES;"
```

Detener MariaDB sin borrar datos:

```bash
docker compose down
```

Detener MariaDB borrando el volumen:

```bash
docker compose down -v
```

## Compilar el proyecto

Desde la raiz:

```bash
./mvnw clean package -DskipTests
```

Esto compila:

- `biblioteca-monolito`
- `libro-service`
- `prestamo-service`

## Ejecutar los microservicios

Primero debe estar levantado MariaDB con Docker.

Terminal 1:

```bash
cd libro-service
./mvnw spring-boot:run
```

Terminal 2:

```bash
cd prestamo-service
./mvnw spring-boot:run
```

Orden recomendado:

1. Levantar MariaDB.
2. Levantar `libro-service`.
3. Levantar `prestamo-service`.

`prestamo-service` necesita que `libro-service` este disponible para validar libros mediante OpenFeign.

## Ejecutar el monolito

El monolito se mantiene como proyecto independiente.

Terminal separada:

```bash
cd biblioteca-monolito
./mvnw spring-boot:run
```

Puerto:

```text
http://localhost:8083
```

Nota: el monolito usa la base `prestamolibros_db` con usuario `root`, segun su `application.properties`.

## Swagger

Microservicios:

- `http://localhost:8081/swagger-ui.html`
- `http://localhost:8082/swagger-ui.html`

Monolito:

- `http://localhost:8083/swagger-ui.html`

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

Regla de seguridad:

- `GET /api/v1/**` es publico.
- `POST`, `PUT` y `DELETE` requieren `Authorization: Bearer <token>`.

## Endpoints principales

`libro-service`:

- `GET /api/v1/libros`
- `GET /api/v1/libros/{id}`
- `POST /api/v1/libros`
- `PUT /api/v1/libros/{id}`
- `DELETE /api/v1/libros/{id}`

`prestamo-service`:

- `GET /api/v1/prestamos`
- `GET /api/v1/prestamos/{id}`
- `POST /api/v1/prestamos`
- `PUT /api/v1/prestamos/{id}`
- `DELETE /api/v1/prestamos/{id}`

## HATEOAS

HATEOAS basico esta implementado en los endpoints `GET`.

Libros:

- `GET /api/v1/libros` responde `CollectionModel` con `_embedded` y `_links`.
- `GET /api/v1/libros/{id}` responde `EntityModel` con `_links`.
- Links actuales: `self` y `todos-los-libros`.

Prestamos:

- `GET /api/v1/prestamos` responde `CollectionModel` con `_embedded` y `_links`.
- `GET /api/v1/prestamos/{id}` responde `EntityModel` con `_links`.
- Links actuales: `self` y `todos-los-prestamos`.

Nota: `POST` y `PUT` actualmente responden DTO plano. Esto no rompe el CRUD ni la pauta basica de HATEOAS en `GET`, pero podria mejorarse si el profesor exige respuestas enlazadas tambien al crear o actualizar.

## OpenFeign

`prestamo-service` se comunica con `libro-service` mediante:

```text
GET http://localhost:8081/api/v1/libros/{id}
```

Configuracion:

```properties
libro-service.url=http://localhost:8081
```

Flujo:

1. El cliente crea o actualiza un prestamo con `libroId`.
2. `prestamo-service` consulta `libro-service`.
3. Si el libro existe, guarda o actualiza el prestamo.
4. Si el libro no existe, responde error controlado.

## Verificar Flyway

Tablas esperadas en `biblioteca_libros_db`:

- `flyway_schema_history`
- `libro`
- `libro_backup`

Tablas esperadas en `biblioteca_prestamos_db`:

- `flyway_schema_history`
- `prestamo`
- `prestamo_backup`

Comandos:

```bash
docker exec -it biblioteca-mariadb mariadb -ubiblioteca_user -pbiblioteca_pass biblioteca_libros_db -e "SHOW TABLES;"
docker exec -it biblioteca-mariadb mariadb -ubiblioteca_user -pbiblioteca_pass biblioteca_prestamos_db -e "SHOW TABLES;"
```

Ver triggers:

```bash
docker exec -it biblioteca-mariadb mariadb -ubiblioteca_user -pbiblioteca_pass -e "SELECT trigger_schema, trigger_name, event_object_table FROM information_schema.triggers WHERE trigger_schema IN ('biblioteca_libros_db','biblioteca_prestamos_db');"
```

## Pruebas pendientes

Actualmente no existen pruebas automaticas en los microservicios.

Pendiente recomendado para cierre academico:

- Pruebas unitarias de services con JUnit 5 y Mockito.
- Pruebas de controllers con MockMvc si la pauta lo exige.
- Pruebas de validaciones de DTOs.
- Pruebas de excepciones controladas.
- Pruebas de `PrestamoService` validando el comportamiento de OpenFeign mockeado.

Prioridad sugerida:

1. `LibroService`
2. `PrestamoService`
3. `LibroController`
4. `PrestamoController`
5. `AuthController`

## Estado de avance estimado

- Arquitectura: 95%
- Microservicios: 95%
- Flyway: 95%
- Swagger: 85%
- JWT: 95%
- OpenFeign: 95%
- HATEOAS: 85%
- Pruebas automaticas: 0%
- Proyecto total: 85% a 90%

## Comandos rapidos de validacion

Compilar:

```bash
./mvnw clean package -DskipTests
```

Ver puertos:

```bash
lsof -nP -iTCP:8081 -sTCP:LISTEN
lsof -nP -iTCP:8082 -sTCP:LISTEN
lsof -nP -iTCP:8083 -sTCP:LISTEN
lsof -nP -iTCP:3306 -sTCP:LISTEN
```

Validar Swagger:

```bash
curl http://localhost:8081/v3/api-docs
curl http://localhost:8082/v3/api-docs
```

Validar HATEOAS:

```bash
curl http://localhost:8081/api/v1/libros
curl http://localhost:8082/api/v1/prestamos
```

En las respuestas deben aparecer `_links`.
