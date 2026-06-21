package com.universidad.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class PrestamoResponseDTO {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaTermino;
    private String libroTitulo;
}
