CREATE TRIGGER despues_insert_prestamo
AFTER INSERT ON prestamo
FOR EACH ROW
INSERT INTO prestamo_backup (id, fecha_inicio, fecha_termino, libro_id)
VALUES (NEW.id, NEW.fecha_inicio, NEW.fecha_termino, NEW.libro_id);
