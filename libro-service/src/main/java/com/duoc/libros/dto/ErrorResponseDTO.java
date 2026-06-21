package com.duoc.libros.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta estándar para errores de la API")
public class ErrorResponseDTO {
    @Schema(description = "Fecha y hora del error", example = "2026-06-18T02:30:51")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP de la respuesta", example = "400")
    private int status;

    @Schema(description = "Nombre del error HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mensaje principal del error", example = "Error de validación")
    private String message;

    @Schema(description = "Detalle adicional de errores de validación")
    private Map<String, String> details;
}
