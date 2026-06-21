package com.universidad.biblioteca.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos requeridos para crear o actualizar un préstamo")
public class PrestamoRequestDTO {

    @Schema(description = "Fecha de inicio del préstamo", example = "2026-06-18")
    @NotNull(message = "La fecha de inicio del préstamo es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a hoy")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de término del préstamo", example = "2026-06-25")
    @NotNull(message = "La fecha de término del préstamo es obligatoria")
    @FutureOrPresent(message = "La fecha de término no puede ser anterior a hoy")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaTermino;

    @Schema(description = "ID del libro asociado al préstamo", example = "1")
    @NotNull(message = "El ID del libro es obligatorio")
    private Long libroId;
}
