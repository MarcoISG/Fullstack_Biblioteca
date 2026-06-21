package com.duoc.libros.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de un libro")
public class LibroResponseDTO {
    @Schema(description = "ID del libro", example = "1")
    private Long id;

    @Schema(description = "Titulo del libro", example = "Clean Code")
    private String titulo;

    @Schema(description = "Autor del libro", example = "Robert C. Martin")
    private String autor;

    @Schema(description = "Editorial del libro", example = "Prentice Hall")
    private String editorial;
}
