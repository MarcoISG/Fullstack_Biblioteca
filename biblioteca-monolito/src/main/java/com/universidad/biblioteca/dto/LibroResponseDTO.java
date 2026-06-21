package com.universidad.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class LibroResponseDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String editorial;
}
