# API Biblioteca

Proyecto backend monolítico en Spring Boot para gestionar libros y prestamos.

## Tecnologias

- Java 21
- Spring Boot Web
- Spring Data JPA
- Validation
- MariaDB
- Flyway
- Lombok
- Swagger / OpenAPI
- Docker Compose para la base de datos

## Ejecucion

1. Inicia MariaDB:

```bash
docker-compose up -d
```

2. Ejecuta la aplicación:

```bash
./mvnw spring-boot:run
```

3. Revisa Swagger:

- [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

## Endpoints

### Libros

- `GET /api/v1/libros`
- `GET /api/v1/libros/{id}`
- `POST /api/v1/libros`
- `PUT /api/v1/libros/{id}`
- `DELETE /api/v1/libros/{id}`

### Préstamos

- `GET /api/v1/prestamos`
- `GET /api/v1/prestamos/{id}`
- `POST /api/v1/prestamos`
- `PUT /api/v1/prestamos/{id}`
- `DELETE /api/v1/prestamos/{id}`
