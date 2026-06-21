package com.duoc.libros.controller;

import com.duoc.libros.dto.ErrorResponseDTO;
import com.duoc.libros.dto.LibroRequestDTO;
import com.duoc.libros.dto.LibroResponseDTO;
import com.duoc.libros.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.duoc.libros.assembler.LibroModelAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@Validated
@Tag(name = "Gestión de Libros", description = "Operaciones relacionadas con la gestión de libros")
public class LibroController {

    private final LibroService libroService;
    private final LibroModelAssembler assembler;

    @Operation(summary = "Obtener todos los libros")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LibroResponseDTO.class))))
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<LibroResponseDTO>>> obtenerTodos() {

        List<EntityModel<LibroResponseDTO>> libros =
                libroService.obtenerTodos()
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        CollectionModel<EntityModel<LibroResponseDTO>> collection =
                CollectionModel.of(
                        libros,
                        linkTo(methodOn(LibroController.class)
                                .obtenerTodos())
                                .withSelfRel()
                );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Obtener libro por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado",
                    content = @Content(schema = @Schema(implementation = LibroResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<LibroResponseDTO>> obtenerPorId(
            @Parameter(description = "ID del libro", example = "1")
            @PathVariable @Positive(message = "El ID del libro debe ser mayor que cero") Long id) {
        return ResponseEntity.ok(assembler.toModel(libroService.obtenerPorId(id)));
    };

    @Operation(summary = "Crear un nuevo libro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro creado correctamente",
                    content = @Content(schema = @Schema(implementation = LibroResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "titulo": "Clean Code",
                                      "autor": "Robert C. Martin",
                                      "editorial": "Prentice Hall"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<LibroResponseDTO> crear(@Valid @RequestBody LibroRequestDTO dto) {
        return ResponseEntity.status(201).body(libroService.guardar(dto));
    }

    @Operation(summary = "Actualizar un libro existente")
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
            @Parameter(description = "ID del libro", example = "1")
            @PathVariable @Positive(message = "El ID del libro debe ser mayor que cero") Long id,
            @Valid @RequestBody LibroRequestDTO dto) {
        return ResponseEntity.ok(libroService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar un libro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Libro eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del libro", example = "1")
            @PathVariable @Positive(message = "El ID del libro debe ser mayor que cero") Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
