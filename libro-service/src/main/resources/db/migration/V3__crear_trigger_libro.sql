CREATE TRIGGER despues_insert_libro
AFTER INSERT ON libro
FOR EACH ROW
INSERT INTO libro_backup (id, titulo, autor, editorial)
VALUES (NEW.id, NEW.titulo, NEW.autor, NEW.editorial);
