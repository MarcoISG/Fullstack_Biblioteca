package com.duoc.libros.assembler;

import com.duoc.libros.controller.LibroController;
import com.duoc.libros.dto.LibroResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class LibroModelAssembler
        implements RepresentationModelAssembler<LibroResponseDTO, EntityModel<LibroResponseDTO>> {

    @Override
    public EntityModel<LibroResponseDTO> toModel(LibroResponseDTO libro) {

        return EntityModel.of(
                libro,

                linkTo(methodOn(LibroController.class)
                        .obtenerPorId(libro.getId()))
                        .withSelfRel(),

                linkTo(methodOn(LibroController.class)
                        .obtenerTodos())
                        .withRel("todos-los-libros")
        );
    }
}