# Preparacion y Migracion a Microservicios segun Material del Profesor

## Objetivo

Preparar y migrar el proyecto actual `biblioteca` desde un monolito Spring Boot hacia dos microservicios independientes:

- `libro-service`
- `prestamo-service`

Esta guia esta basada en los archivos entregados por el profesor:

- `Comunicacion_microservicio.pptx`
- `Comunicacion entre microservicios.docx`
- `MIGRACION DE BASE DE DATOS.docx`
- `migraciones.zip`

El enfoque principal del material es:

- separar servicios
- usar una base de datos por servicio
- conectar servicios con REST, preferiblemente OpenFeign
- manejar migraciones con Flyway
- usar backups antes de cambios importantes
- tener una estrategia de reparacion o rollback manual

## Estado actual del proyecto

El proyecto actual ya tiene una buena base para migrar:

- Spring Boot
- Spring Web
- Spring Data JPA
- MariaDB
- Flyway
- DTOs
- Controllers
- Services
- Repositories
- Validaciones
- Manejo global de errores
- Swagger
- Spring Security
- JWT

Actualmente todo vive en un solo proyecto:

```text
com.universidad.biblioteca
```

Y los dos dominios principales son:

- `Libro`
- `Prestamo`

## Meta de arquitectura

La estructura final esperada, siguiendo el material del profesor, deberia quedar asi:

```text
biblioteca-microservicios/

libro-service/
  src/main/java/com/universidad/libros/
    controller/
    dto/
    exception/
    model/
    repository/
    service/
  src/main/resources/db/migration/
  pom.xml

prestamo-service/
  src/main/java/com/universidad/prestamos/
    client/
    controller/
    dto/
    exception/
    model/
    repository/
    service/
  src/main/resources/db/migration/
  pom.xml
```

## Puertos recomendados

Segun el ejemplo del profesor, cada microservicio debe correr en un puerto distinto.

Para este proyecto:

```properties
libro-service     -> server.port=8081
prestamo-service  -> server.port=8082
```

El monolito actual usa `8083`, por lo que se puede dejar como referencia mientras se realiza la migracion.

## Bases de datos separadas

El material indica el principio de **Database per Service**.

Por eso, se deben crear dos bases de datos en Laragon, MariaDB o MySQL:

```sql
CREATE DATABASE biblioteca_libros_db;
CREATE DATABASE biblioteca_prestamos_db;
```

Cada servicio debe conectarse solo a su propia base de datos.

## Paso 1: Crear `libro-service`

Crear un nuevo proyecto Spring Boot para libros.

### Dependencias necesarias

El `pom.xml` de `libro-service` debe incluir, como minimo:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

Si se mantiene seguridad desde el proyecto actual, tambien agregar:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.7</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.7</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.7</version>
    <scope>runtime</scope>
</dependency>
```

### Configuracion `application.properties`

```properties
spring.application.name=libro-service
server.port=8081

spring.datasource.url=jdbc:mariadb://localhost:3306/biblioteca_libros_db
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

### Clases que pasan a `libro-service`

Desde el monolito actual, mover o recrear:

- `Libro`
- `LibroRequestDTO`
- `LibroResponseDTO`
- `LibroRepository`
- `LibroService`
- `LibroController`
- excepciones necesarias
- `GlobalExceptionHandler`
- configuracion Swagger si se mantiene documentacion
- configuracion Security/JWT si se mantiene seguridad

### Endpoints que debe exponer

```text
GET    /api/v1/libros
GET    /api/v1/libros/{id}
POST   /api/v1/libros
PUT    /api/v1/libros/{id}
DELETE /api/v1/libros/{id}
```

El endpoint mas importante para la comunicacion entre servicios sera:

```text
GET /api/v1/libros/{id}
```

Porque `prestamo-service` lo usara para validar que un libro existe.

## Paso 2: Crear migraciones Flyway para `libro-service`

En:

```text
libro-service/src/main/resources/db/migration/
```

Crear:

```text
V1__crear_tabla_libro.sql
```

Contenido esperado:

```sql
CREATE TABLE libro (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(100) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    editorial VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);
```

Si se mantienen tablas de respaldo o triggers, deben quedar solo si el profesor los pidio o si forman parte de la entrega de migracion:

```text
V2__crear_tabla_libros_backup.sql
V3__crear_trigger_libro.sql
```

## Paso 3: Crear `prestamo-service`

Crear un segundo proyecto Spring Boot para prestamos.

### Dependencias necesarias

Debe tener las mismas dependencias base:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

Ademas, segun el material del profesor, para comunicacion entre microservicios se recomienda OpenFeign:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

Nota: Spring Cloud normalmente requiere declarar una version compatible mediante `dependencyManagement`. Antes de implementar, hay que validar la version compatible con la version de Spring Boot usada por el proyecto.

### Configuracion `application.properties`

```properties
spring.application.name=prestamo-service
server.port=8082

spring.datasource.url=jdbc:mariadb://localhost:3306/biblioteca_prestamos_db
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

libro-service.url=http://localhost:8081
```

## Paso 4: Ajustar el modelo `Prestamo`

En el monolito actual, `Prestamo` tiene una relacion JPA directa con `Libro`.

Eso sirve en monolito, pero no corresponde al enfoque de microservicios con base de datos por servicio.

### Modelo actual conceptual

```java
@ManyToOne
@JoinColumn(name = "libro_id", nullable = false)
private Libro libro;
```

### Modelo preparado para microservicio

En `prestamo-service`, `Prestamo` debe guardar solo el identificador del libro:

```java
@Column(name = "libro_id", nullable = false)
private Long libroId;
```

### Por que

Porque `prestamo-service` ya no tendra acceso a la tabla `libro`.

La existencia del libro se valida consultando a `libro-service`.

## Paso 5: Crear migraciones Flyway para `prestamo-service`

En:

```text
prestamo-service/src/main/resources/db/migration/
```

Crear:

```text
V1__crear_tabla_prestamo.sql
```

Contenido esperado:

```sql
CREATE TABLE prestamo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha_inicio DATE NOT NULL,
    fecha_termino DATE NOT NULL,
    libro_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
```

### Cambio importante respecto al monolito

En microservicios no se debe crear esta llave foranea:

```sql
FOREIGN KEY (libro_id) REFERENCES libro(id)
```

Porque la tabla `libro` estara en otra base de datos.

La validacion se hace por comunicacion entre servicios.

Si se mantienen tablas de respaldo o triggers:

```text
V2__crear_tabla_prestamo_backup.sql
V3__crear_trigger_prestamo.sql
```

## Paso 6: Habilitar OpenFeign en `prestamo-service`

En la clase principal de `prestamo-service`, agregar:

```java
@SpringBootApplication
@EnableFeignClients
public class PrestamoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrestamoServiceApplication.class, args);
    }
}
```

Import necesario:

```java
import org.springframework.cloud.openfeign.EnableFeignClients;
```

## Paso 7: Crear cliente Feign para consultar libros

Crear el paquete:

```text
src/main/java/com/universidad/prestamos/client/
```

Crear:

```text
LibroClient.java
```

Ejemplo:

```java
@FeignClient(name = "libro-service", url = "${libro-service.url}")
public interface LibroClient {

    @GetMapping("/api/v1/libros/{id}")
    LibroResponseDTO obtenerPorId(@PathVariable("id") Long id);
}
```

Este cliente representa la comunicacion que el profesor muestra entre microservicios.

`prestamo-service` llamara a `libro-service` como si fuera un metodo local.

## Paso 8: Crear DTO minimo de libro dentro de `prestamo-service`

`prestamo-service` no debe importar clases Java desde `libro-service`.

Debe tener su propio DTO para recibir la respuesta remota.

Crear, por ejemplo:

```text
src/main/java/com/universidad/prestamos/dto/LibroResponseDTO.java
```

Con los campos necesarios:

```java
private Long id;
private String titulo;
private String autor;
private String editorial;
```

Este DTO representa el contrato HTTP, no una entidad JPA.

## Paso 9: Usar `LibroClient` dentro de `PrestamoService`

Antes de crear o actualizar un prestamo, `prestamo-service` debe validar que el libro existe.

Flujo esperado:

1. El usuario llama a `POST /api/v1/prestamos`.
2. `PrestamoService` recibe `libroId`.
3. `PrestamoService` llama a `LibroClient.obtenerPorId(libroId)`.
4. Si `libro-service` responde correctamente, se crea el prestamo.
5. Si `libro-service` responde error, el prestamo no se crea.

Esto corresponde al ejemplo del profesor donde un servicio consulta a otro antes de aplicar su logica de negocio.

## Paso 10: Ajustar DTOs de prestamo

`PrestamoRequestDTO` debe seguir usando:

```java
private Long libroId;
```

`PrestamoResponseDTO` deberia devolver:

```java
private Long id;
private LocalDate fechaInicio;
private LocalDate fechaTermino;
private Long libroId;
private String tituloLibro;
```

### Por que agregar `libroId`

Porque en microservicios el identificador del libro es el dato que conecta ambos servicios.

`tituloLibro` puede venir desde la respuesta de `libro-service`.

## Paso 11: Mantener endpoints de `prestamo-service`

```text
GET    /api/v1/prestamos
GET    /api/v1/prestamos/{id}
POST   /api/v1/prestamos
PUT    /api/v1/prestamos/{id}
DELETE /api/v1/prestamos/{id}
```

En `POST` y `PUT`, el servicio debe validar el libro contra `libro-service`.

## Paso 12: Alternativa con RestTemplate

El material tambien muestra una opcion con `RestTemplate`.

Esta opcion funciona, pero el propio material presenta OpenFeign como una forma mas limpia y mantenible.

Si se usara `RestTemplate`, `prestamo-service` necesitaria:

- un `Bean` de `RestTemplate`
- construir la URL de `libro-service`
- llamar manualmente al endpoint remoto

Ejemplo conceptual:

```java
String url = "http://localhost:8081/api/v1/libros/" + libroId;
LibroResponseDTO libro = restTemplate.getForObject(url, LibroResponseDTO.class);
```

Para este proyecto, la opcion recomendada es OpenFeign.

## Paso 13: Seguridad entre servicios

El proyecto actual ya tiene Spring Security y JWT.

Al separar los servicios, hay dos caminos:

- mantener seguridad en ambos servicios
- dejar publicos temporalmente los endpoints internos durante la practica

Para una entrega mas consistente, conviene mantener seguridad en ambos servicios.

Si `GET /api/v1/libros/{id}` queda protegido, entonces `prestamo-service` debera enviar token al llamar a `libro-service`.

Para una primera migracion academica, se puede mantener publico el `GET /api/v1/libros/{id}` igual que en el monolito actual, donde los `GET` ya estan permitidos.

## Paso 14: Logs

El material menciona manejo de logs.

Cada servicio deberia registrar:

- consultas principales
- creacion de registros
- actualizaciones
- eliminaciones
- errores de validacion
- errores al comunicarse con otro servicio

Ejemplos aplicados:

- `libro-service`: "Consultando libro con id X"
- `prestamo-service`: "Validando existencia de libro X en libro-service"
- `prestamo-service`: "No fue posible consultar libro-service"

## Paso 15: Backup antes de migraciones importantes

El documento de migracion indica usar `mysqldump` antes de cambios de alto impacto.

Para este proyecto, crear respaldos separados:

```text
backup_biblioteca_libros_db.sql
backup_biblioteca_prestamos_db.sql
```

Ejemplo conceptual:

```bash
mysqldump -u root --databases biblioteca_libros_db -r backup_biblioteca_libros_db.sql
mysqldump -u root --databases biblioteca_prestamos_db -r backup_biblioteca_prestamos_db.sql
```

En Windows con Laragon, se debe usar la ruta real del binario `mysqldump`, como muestra el material del profesor.

## Paso 16: Automatizar backup si se solicita

El documento muestra dos clases:

- `BackupService`
- `BackupScheduler`

Aplicado a este proyecto, se podria crear un servicio de backup por microservicio.

Ejemplo de responsabilidad:

- `libro-service`: backup de `biblioteca_libros_db`
- `prestamo-service`: backup de `biblioteca_prestamos_db`

El scheduler podria ejecutarse diariamente:

```java
@Scheduled(cron = "0 0 0 * * ?")
```

## Paso 17: Rollback y reparacion con Flyway

El material indica que en Flyway Community el rollback automatico no es lo principal.

La estrategia esperada es:

1. identificar la migracion fallida
2. crear o ejecutar un script manual para deshacer el cambio si corresponde
3. reparar el historial de Flyway
4. volver a ejecutar la aplicacion

Si Flyway queda en estado fallido, se puede usar:

```java
flyway.repair();
```

O limpiar manualmente una entrada fallida en:

```text
flyway_schema_history
```

## Paso 18: Regla recomendada por el material

El documento destaca una idea importante:

```text
Forward Only
```

Esto significa que muchas veces es mejor crear una nueva migracion que corrija el problema, en vez de intentar retroceder toda la base de datos.

Ejemplo:

```text
V3__fix_previous_error.sql
```

## Paso 19: Orden concreto para este proyecto

Orden sugerido para migrar sin perder control:

1. crear base `biblioteca_libros_db`
2. crear base `biblioteca_prestamos_db`
3. crear proyecto `libro-service`
4. mover codigo de libros al nuevo servicio
5. crear migraciones Flyway de libros
6. levantar `libro-service` en puerto `8081`
7. probar `GET /api/v1/libros/{id}`
8. crear proyecto `prestamo-service`
9. mover codigo de prestamos al nuevo servicio
10. cambiar `Prestamo` para guardar `libroId` en vez de relacion JPA con `Libro`
11. crear migraciones Flyway de prestamos sin foreign key hacia `libro`
12. agregar OpenFeign a `prestamo-service`
13. crear `LibroClient`
14. usar `LibroClient` para validar libros al crear o actualizar prestamos
15. levantar `prestamo-service` en puerto `8082`
16. probar flujo completo creando un prestamo con un `libroId` existente
17. probar error creando un prestamo con un `libroId` inexistente
18. revisar logs
19. crear backup de ambas bases
20. documentar rollback o reparacion Flyway

## Paso 20: Pruebas manuales esperadas

### Probar `libro-service`

```text
GET http://localhost:8081/api/v1/libros
GET http://localhost:8081/api/v1/libros/1
POST http://localhost:8081/api/v1/libros
PUT http://localhost:8081/api/v1/libros/1
DELETE http://localhost:8081/api/v1/libros/1
```

### Probar `prestamo-service`

```text
GET http://localhost:8082/api/v1/prestamos
GET http://localhost:8082/api/v1/prestamos/1
POST http://localhost:8082/api/v1/prestamos
PUT http://localhost:8082/api/v1/prestamos/1
DELETE http://localhost:8082/api/v1/prestamos/1
```

### Flujo clave

Crear un prestamo debe provocar esta comunicacion:

```text
cliente -> prestamo-service -> libro-service -> prestamo-service -> base biblioteca_prestamos_db
```

## Que no corresponde agregar todavia

El material entregado no exige necesariamente:

- API Gateway
- Eureka Discovery Server
- Kubernetes
- Docker Compose para multiples servicios
- RabbitMQ o Kafka
- Circuit Breaker
- HATEOAS

Esas tecnologias pueden venir despues, pero no son el foco principal de estos archivos.

## Resumen final

Segun el material del profesor, la preparacion correcta para este proyecto es:

- separar `Libro` y `Prestamo` en dos servicios
- dar una base de datos propia a cada servicio
- mantener migraciones Flyway por servicio
- eliminar dependencia JPA directa entre `Prestamo` y `Libro`
- comunicar `prestamo-service` con `libro-service` usando OpenFeign
- validar reglas de negocio por comunicacion HTTP
- respaldar bases con `mysqldump`
- manejar errores de migracion con scripts manuales y `flyway.repair()`

El cambio mas importante a nivel de diseno es:

```text
Prestamo ya no debe depender de la entidad Libro.
Prestamo debe guardar libroId y consultar a libro-service cuando necesite validar o mostrar datos del libro.
```
