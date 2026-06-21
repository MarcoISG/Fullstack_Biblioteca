package com.duoc.prestamos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear o actualizar un préstamo")
public class PrestamoRequestDTO {

    @Schema(description = "Fecha de inicio del préstamo", example = "2027-01-10")
    @NotNull(message = "La fecha de inicio del préstamo es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a hoy")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de término del préstamo", example = "2027-01-20")
    @NotNull(message = "La fecha de término del préstamo es obligatoria")
    @FutureOrPresent(message = "La fecha de término no puede ser anterior a hoy")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaTermino;

    @Schema(description = "ID del libro asociado al préstamo", example = "1")
    @NotNull(message = "El ID del libro es obligatorio")
    @Positive(message = "El ID del libro debe ser mayor que cero")
    private Long libroId;
}
