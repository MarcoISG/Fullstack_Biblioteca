package com.duoc.libros.service;

import com.duoc.libros.dto.LibroRequestDTO;
import com.duoc.libros.dto.LibroResponseDTO;
import com.duoc.libros.exception.ResourceNotFoundException;
import com.duoc.libros.model.Libro;
import com.duoc.libros.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    private final LibroRepository libroRepository;

    private LibroResponseDTO mapToDTO(Libro libro) {
        return new LibroResponseDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getEditorial()
        );
    }

    public List<LibroResponseDTO> obtenerTodos() {
        log.info("Consultando lista de libros");
        return libroRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public LibroResponseDTO obtenerPorId(Long id) {
        log.info("Consultando libro con id {}", id);
        return libroRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

    public LibroResponseDTO guardar(LibroRequestDTO dto) {
        log.info("Guardando libro con titulo {}", dto.getTitulo());
        Libro libro = new Libro(null, dto.getTitulo(), dto.getAutor(), dto.getEditorial());
        return mapToDTO(libroRepository.save(libro));
    }

    public LibroResponseDTO actualizar(Long id, LibroRequestDTO dto) {
        log.info("Actualizando libro con id {}", id);
        return libroRepository.findById(id).map(existente -> {
            existente.setTitulo(dto.getTitulo());
            existente.setAutor(dto.getAutor());
            existente.setEditorial(dto.getEditorial());
            return mapToDTO(libroRepository.save(existente));
        }).orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        log.info("Eliminando libro con id {}", id);
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + id);
        }
        libroRepository.deleteById(id);
    }
}
