package com.duoc.prestamos.controller;

import com.duoc.prestamos.assembler.PrestamoModelAssembler;
import com.duoc.prestamos.dto.ErrorResponseDTO;
import com.duoc.prestamos.dto.PrestamoRequestDTO;
import com.duoc.prestamos.dto.PrestamoResponseDTO;
import com.duoc.prestamos.service.PrestamoService;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Gestión de préstamos", description = "Endpoints para la gestión de préstamos de libros")
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final PrestamoModelAssembler assembler;

    @Operation(summary = "Obtener todos los préstamos")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestamoResponseDTO.class))))
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PrestamoResponseDTO>>> obtenerTodos() {

        List<EntityModel<PrestamoResponseDTO>> prestamos =
                prestamoService.obtenerTodos()
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        CollectionModel<EntityModel<PrestamoResponseDTO>> collection =
                CollectionModel.of(
                        prestamos,
                        linkTo(methodOn(PrestamoController.class)
                                .obtenerTodos())
                                .withSelfRel()
                );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Obtener préstamo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo encontrado",
                    content = @Content(schema = @Schema(implementation = PrestamoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PrestamoResponseDTO>> obtenerPorId(
            @Parameter(description = "ID del préstamo", example = "1")
            @PathVariable @Positive(message = "El ID del préstamo debe ser mayor que cero") Long id) {
        return ResponseEntity.ok(
                assembler.toModel(
                        prestamoService.obtenerPorId(id)
                )
        );
    }

    @Operation(summary = "Crear un nuevo préstamo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Préstamo creado correctamente",
                    content = @Content(schema = @Schema(implementation = PrestamoResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "fechaInicio": "2027-01-10",
                                      "fechaTermino": "2027-01-20",
                                      "libroId": 1,
                                      "tituloLibro": "Clean Code"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> crear(@Valid @RequestBody PrestamoRequestDTO dto) {
        return ResponseEntity.status(201).body(prestamoService.guardar(dto));
    }

    @Operation(summary = "Actualizar un préstamo existente")
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
            @Parameter(description = "ID del préstamo", example = "1")
            @PathVariable @Positive(message = "El ID del préstamo debe ser mayor que cero") Long id,
            @Valid @RequestBody PrestamoRequestDTO dto) {
        return ResponseEntity.ok(prestamoService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar un préstamo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Préstamo eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del préstamo", example = "1")
            @PathVariable @Positive(message = "El ID del préstamo debe ser mayor que cero") Long id) {
        prestamoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
