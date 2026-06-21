CREATE TABLE prestamo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha_inicio DATE NOT NULL,
    fecha_termino DATE NOT NULL,
    libro_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
