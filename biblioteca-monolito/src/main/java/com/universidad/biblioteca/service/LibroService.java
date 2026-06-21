package com.universidad.biblioteca.service;

import com.universidad.biblioteca.dto.LibroRequestDTO;
import com.universidad.biblioteca.dto.LibroResponseDTO;
import com.universidad.biblioteca.exception.ResourceNotFoundException;
import com.universidad.biblioteca.model.Libro;
import com.universidad.biblioteca.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    private final LibroRepository libroRepository;

    private LibroResponseDTO mapToDTO(Libro m) {
        return new LibroResponseDTO(m.getId(), m.getTitulo(),
                m.getAutor(), m.getEditorial());
    }

    public List<LibroResponseDTO> obtenerTodos() {
        log.info("Consultando lista de libros");
        return libroRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public LibroResponseDTO obtenerPorId(Long id) {
        log.info("Consultando libro con id {}", id);
        return libroRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + id));
    }

    public LibroResponseDTO guardar(LibroRequestDTO dto) {
        log.info("Guardando libro con titulo {}", dto.getTitulo());
        Libro libro = new Libro(null,
                dto.getTitulo(), dto.getAutor(), dto.getEditorial());
        return mapToDTO(libroRepository.save(libro));
    }

    public LibroResponseDTO actualizar(Long id, LibroRequestDTO dto) {
        log.info("Actualizando libro con id {}", id);
        return libroRepository.findById(id).map(existente -> {
            existente.setTitulo(dto.getTitulo());
            existente.setAutor(dto.getAutor());
            existente.setEditorial(dto.getEditorial());
            return mapToDTO(libroRepository.save(existente));
        }).orElseThrow(() -> new ResourceNotFoundException(
                "Libro no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        log.info("Eliminando libro con id {}", id);
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + id);
        }
        libroRepository.deleteById(id);
    }
}
