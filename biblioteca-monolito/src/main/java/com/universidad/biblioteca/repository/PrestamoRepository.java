package com.universidad.biblioteca.repository;

import com.universidad.biblioteca.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
}
