package com.duoc.prestamos.repository;

import com.duoc.prestamos.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
}
