package com.duoc.prestamos.service;

import com.duoc.prestamos.client.LibroClient;
import com.duoc.prestamos.dto.LibroResponseDTO;
import com.duoc.prestamos.dto.PrestamoRequestDTO;
import com.duoc.prestamos.dto.PrestamoResponseDTO;
import com.duoc.prestamos.exception.BadRequestException;
import com.duoc.prestamos.exception.ResourceNotFoundException;
import com.duoc.prestamos.model.Prestamo;
import com.duoc.prestamos.repository.PrestamoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository prestamoRepository;
    private final LibroClient libroClient;

    private PrestamoResponseDTO mapToDTO(Prestamo prestamo) {
        LibroResponseDTO libro = obtenerLibro(prestamo.getLibroId());
        return new PrestamoResponseDTO(
                prestamo.getId(),
                prestamo.getFechaInicio(),
                prestamo.getFechaTermino(),
                prestamo.getLibroId(),
                libro.getTitulo()
        );
    }

    public List<PrestamoResponseDTO> obtenerTodos() {
        log.info("Consultando lista de préstamos");
        return prestamoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public PrestamoResponseDTO obtenerPorId(Long id) {
        log.info("Consultando préstamo con id {}", id);
        return prestamoRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));
    }

    public PrestamoResponseDTO guardar(PrestamoRequestDTO dto) {
        log.info("Guardando préstamo para libro con id {}", dto.getLibroId());
        validarFechas(dto);
        LibroResponseDTO libro = obtenerLibro(dto.getLibroId());

        Prestamo prestamo = new Prestamo(null, dto.getFechaInicio(), dto.getFechaTermino(), libro.getId());
        Prestamo guardado = prestamoRepository.save(prestamo);

        return new PrestamoResponseDTO(
                guardado.getId(),
                guardado.getFechaInicio(),
                guardado.getFechaTermino(),
                guardado.getLibroId(),
                libro.getTitulo()
        );
    }

    public PrestamoResponseDTO actualizar(Long id, PrestamoRequestDTO dto) {
        log.info("Actualizando préstamo con id {}", id);
        validarFechas(dto);
        LibroResponseDTO libro = obtenerLibro(dto.getLibroId());

        return prestamoRepository.findById(id).map(existente -> {
            existente.setFechaInicio(dto.getFechaInicio());
            existente.setFechaTermino(dto.getFechaTermino());
            existente.setLibroId(libro.getId());
            Prestamo actualizado = prestamoRepository.save(existente);
            return new PrestamoResponseDTO(
                    actualizado.getId(),
                    actualizado.getFechaInicio(),
                    actualizado.getFechaTermino(),
                    actualizado.getLibroId(),
                    libro.getTitulo()
            );
        }).orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));
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
            throw new BadRequestException("La fecha de término no puede ser anterior a la fecha de inicio");
        }
    }

    private LibroResponseDTO obtenerLibro(Long libroId) {
        try {
            return libroClient.obtenerPorId(libroId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + libroId);
        } catch (FeignException ex) {
            throw new BadRequestException("No fue posible validar el libro con id: " + libroId);
        }
    }
}
