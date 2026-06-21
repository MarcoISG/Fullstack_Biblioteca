package com.duoc.libros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El titulo no puede estar vacío")
    @Size(max = 100, message = "El titulo no puede tener más de 100 caracteres")
    private String titulo;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El autor no puede estar vacío")
    @Size(max = 100, message = "El autor no puede tener más de 100 caracteres")
    private String autor;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La editorial no puede estar vacía")
    @Size(max = 100, message = "La editorial no puede tener más de 100 caracteres")
    private String editorial;
}
