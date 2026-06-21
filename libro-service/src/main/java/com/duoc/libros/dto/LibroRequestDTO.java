package com.duoc.libros.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear o actualizar un libro")
public class LibroRequestDTO {

    @Schema(description = "Titulo del libro", example = "Clean Code")
    @NotBlank(message = "El titulo no puede estar vacío")
    @Size(max = 100, message = "El titulo no puede tener más de 100 caracteres")
    private String titulo;

    @Schema(description = "Autor del libro", example = "Robert C. Martin")
    @NotBlank(message = "El autor no puede estar vacío")
    @Size(max = 100, message = "El autor no puede tener más de 100 caracteres")
    private String autor;

    @Schema(description = "Editorial del libro", example = "Prentice Hall")
    @NotBlank(message = "La editorial no puede estar vacía")
    @Size(max = 100, message = "La editorial no puede tener más de 100 caracteres")
    private String editorial;
}
