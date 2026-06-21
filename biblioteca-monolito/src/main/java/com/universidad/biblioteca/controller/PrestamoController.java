package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.dto.ErrorResponseDTO;
import com.universidad.biblioteca.dto.PrestamoRequestDTO;
import com.universidad.biblioteca.dto.PrestamoResponseDTO;
import com.universidad.biblioteca.service.PrestamoService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/prestamos")
@RequiredArgsConstructor
@Tag(
    name = "Gestión de préstamos",
    description = "Endpoints para la gestión de préstamos de libros en la biblioteca"
)

public class PrestamoController {

    private final PrestamoService prestamoService;

    @Operation(
        summary = "Obtener todos los préstamos", 
        description = "Devuelve una lista de todos los préstamos registrados en la biblioteca"
    )
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestamoResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(prestamoService.obtenerTodos());
    }

    @Operation(
        summary = "Obtener préstamo por ID", 
        description = "Devuelve los detalles de un préstamo específico dado su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo encontrado",
                    content = @Content(schema = @Schema(implementation = PrestamoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPorId(id));
    }

    @Operation(
        summary = "Crear un nuevo préstamo", 
        description = "Permite registrar un nuevo préstamo de libro proporcionando los detalles necesarios"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Préstamo creado correctamente",
                    content = @Content(schema = @Schema(implementation = PrestamoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> crear(
            @Valid @RequestBody PrestamoRequestDTO dto) {
        return ResponseEntity.status(201).body(prestamoService.guardar(dto));
    }

    @Operation(
        summary = "Actualizar un préstamo existente",
        description = "Permite actualizar los detalles de un préstamo existente dado su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = PrestamoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Préstamo o libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody PrestamoRequestDTO dto) {
        return ResponseEntity.ok(prestamoService.actualizar(id, dto));
    }

    @Operation(
        summary = "Eliminar un préstamo existente",
        description = "Permite eliminar un préstamo existente dado su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Préstamo eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        prestamoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
