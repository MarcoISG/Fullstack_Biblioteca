CREATE TABLE prestamo_backup (
    id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_termino DATE NOT NULL,
    libro_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
