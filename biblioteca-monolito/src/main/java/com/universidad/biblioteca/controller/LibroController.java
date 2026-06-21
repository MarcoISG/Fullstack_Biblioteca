package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.dto.ErrorResponseDTO;
import com.universidad.biblioteca.dto.LibroRequestDTO;
import com.universidad.biblioteca.dto.LibroResponseDTO;
import com.universidad.biblioteca.service.LibroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@Tag(
    name = "Gestión de Libros",
    description = "Operaciones relacionadas con la gestión de libros"
)
public class LibroController {

    private final LibroService libroService;
    @Operation(
        summary = "Obtener todos los libros", 
        description = "Devuelve una lista de todos los libros disponibles en la biblioteca"
    )
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LibroResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<LibroResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(libroService.obtenerTodos());
    }

    @Operation(
        summary = "Obtener libro por ID", 
        description = "Devuelve los detalles de un libro específico dado su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado",
                    content = @Content(schema = @Schema(implementation = LibroResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<LibroResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerPorId(id));
    }

    @Operation(
        summary = "Crear un nuevo libro", 
        description = "Permite agregar un nuevo libro a la biblioteca proporcionando los detalles necesarios"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro creado correctamente",
                    content = @Content(schema = @Schema(implementation = LibroResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<LibroResponseDTO> crear(
            @Valid @RequestBody LibroRequestDTO dto) {
        return ResponseEntity.status(201).body(libroService.guardar(dto));
    }

    @Operation(
        summary = "Actualizar un libro existente", 
        description = "Permite actualizar los detalles de un libro existente dado su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = LibroResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<LibroResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody LibroRequestDTO dto) {
        return ResponseEntity.ok(libroService.actualizar(id, dto));
    }

    @Operation(
        summary = "Eliminar un libro", 
        description = "Permite eliminar un libro de la biblioteca dado su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Libro eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
