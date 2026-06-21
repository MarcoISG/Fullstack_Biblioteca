package com.universidad.biblioteca.service;

import com.universidad.biblioteca.dto.PrestamoRequestDTO;
import com.universidad.biblioteca.dto.PrestamoResponseDTO;
import com.universidad.biblioteca.exception.BadRequestException;
import com.universidad.biblioteca.exception.ResourceNotFoundException;
import com.universidad.biblioteca.model.Libro;
import com.universidad.biblioteca.model.Prestamo;
import com.universidad.biblioteca.repository.LibroRepository;
import com.universidad.biblioteca.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    private PrestamoResponseDTO mapToDTO(Prestamo p) {
        return new PrestamoResponseDTO(
                p.getId(), p.getFechaInicio(), p.getFechaTermino(),
                p.getLibro().getTitulo());
    }

    public List<PrestamoResponseDTO> obtenerTodos() {
        log.info("Consultando lista de préstamos");
        return prestamoRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public PrestamoResponseDTO obtenerPorId(Long id) {
        log.info("Consultando préstamo con id {}", id);
        return prestamoRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Préstamo no encontrado con id: " + id));
    }

    public PrestamoResponseDTO guardar(PrestamoRequestDTO dto) {
        log.info("Guardando préstamo para libro con id {}", dto.getLibroId());
        validarFechas(dto);
        Libro libro = libroRepository.findById(dto.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + dto.getLibroId()));
        Prestamo prestamo = new Prestamo (null,
                dto.getFechaInicio(), dto.getFechaTermino(), libro);
        return mapToDTO(prestamoRepository.save(prestamo));
    }

    public PrestamoResponseDTO actualizar(Long id, PrestamoRequestDTO dto) {
        log.info("Actualizando préstamo con id {}", id);
        validarFechas(dto);
        Libro libro = libroRepository.findById(dto.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Libro no encontrado con id: " + dto.getLibroId()));

        return prestamoRepository.findById(id).map(existente -> {
            existente.setFechaInicio(dto.getFechaInicio());
            existente.setFechaTermino(dto.getFechaTermino());
            existente.setLibro(libro);
            return mapToDTO(prestamoRepository.save(existente));
        }).orElseThrow(() -> new ResourceNotFoundException(
                "Préstamo no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        log.info("Eliminando préstamo con id {}", id);
        if (!prestamoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Préstamo no encontrado con id: " + id);
        }
        prestamoRepository.deleteById(id);
    }

    private void validarFechas(PrestamoRequestDTO dto) {
        if (dto.getFechaTermino().isBefore(dto.getFechaInicio())) {
            log.warn("Validación de fechas fallida para libro con id {}", dto.getLibroId());
            throw new BadRequestException(
                    "La fecha de término no puede ser anterior a la fecha de inicio");
        }
    }
}
