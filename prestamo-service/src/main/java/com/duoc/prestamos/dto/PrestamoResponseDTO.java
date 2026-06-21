package com.duoc.prestamos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de un préstamo")
public class PrestamoResponseDTO {
    @Schema(description = "ID del préstamo", example = "1")
    private Long id;

    @Schema(description = "Fecha de inicio del préstamo", example = "2027-01-10")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de término del préstamo", example = "2027-01-20")
    private LocalDate fechaTermino;

    @Schema(description = "ID del libro validado en libro-service", example = "1")
    private Long libroId;

    @Schema(description = "Titulo del libro obtenido desde libro-service", example = "Clean Code")
    private String tituloLibro;
}
