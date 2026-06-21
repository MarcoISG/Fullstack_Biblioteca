package com.duoc.prestamos.assembler;

import com.duoc.prestamos.controller.PrestamoController;
import com.duoc.prestamos.dto.PrestamoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PrestamoModelAssembler
        implements RepresentationModelAssembler<PrestamoResponseDTO, EntityModel<PrestamoResponseDTO>> {

    @Override
    public EntityModel<PrestamoResponseDTO> toModel(PrestamoResponseDTO prestamo) {

        return EntityModel.of(
                prestamo,

                linkTo(methodOn(PrestamoController.class)
                        .obtenerPorId(prestamo.getId()))
                        .withSelfRel(),

                linkTo(methodOn(PrestamoController.class)
                        .obtenerTodos())
                        .withRel("todos-los-prestamos")
        );
    }
}
