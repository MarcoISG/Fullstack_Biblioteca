package com.universidad.biblioteca.repository;

import com.universidad.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> { }
