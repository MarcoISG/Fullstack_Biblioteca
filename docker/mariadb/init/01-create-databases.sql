CREATE DATABASE IF NOT EXISTS biblioteca_libros_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS biblioteca_prestamos_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON biblioteca_libros_db.* TO 'biblioteca_user'@'%';
GRANT ALL PRIVILEGES ON biblioteca_prestamos_db.* TO 'biblioteca_user'@'%';
FLUSH PRIVILEGES;
