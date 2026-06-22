package com.duoc.prestamos.service;

import com.duoc.prestamos.client.LibroClient;
import com.duoc.prestamos.exception.BadRequestException;
import com.duoc.prestamos.exception.ResourceNotFoundException;
import com.duoc.prestamos.repository.PrestamoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.duoc.prestamos.dto.PrestamoRequestDTO;
import com.duoc.prestamos.dto.LibroResponseDTO;
import com.duoc.prestamos.dto.PrestamoResponseDTO;
import com.duoc.prestamos.model.Prestamo;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private LibroClient libroClient;

    @InjectMocks
    private PrestamoService prestamoService;

    @Test
    void guardarPrestamoCorrectamenteCuandoLibroExiste() {
        PrestamoRequestDTO request = new PrestamoRequestDTO();
        request.setLibroId(1L);
        request.setFechaInicio(LocalDate.now());
        request.setFechaTermino(LocalDate.now().plusDays(7));

        LibroResponseDTO libroResponse = new LibroResponseDTO();
        libroResponse.setId(1L);
        libroResponse.setTitulo("Clean Code");
        libroResponse.setAutor("Robert C. Martin");
        libroResponse.setEditorial("Prentice Hall");

        Prestamo prestamoGuardado = new Prestamo();
        prestamoGuardado.setId(1L);
        prestamoGuardado.setLibroId(1L);
        prestamoGuardado.setFechaInicio(request.getFechaInicio());
        prestamoGuardado.setFechaTermino(request.getFechaTermino());

        when(libroClient.obtenerPorId(1L)).thenReturn(libroResponse);
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoGuardado);

        PrestamoResponseDTO resultado = prestamoService.guardar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getLibroId());
        assertEquals("Clean Code", resultado.getTituloLibro());
        assertEquals(request.getFechaInicio(), resultado.getFechaInicio());
        assertEquals(request.getFechaTermino(), resultado.getFechaTermino());

        verify(libroClient, times(1)).obtenerPorId(1L);
        verify(prestamoRepository, times(1)).save(any(Prestamo.class));
    }

    @Test
    void guardarPrestamoConLibroInexistenteLanzaExcepcion() {
        // aquí va la prueba
    }

    @Test
    void guardarPrestamoConFechaTerminoMenorAFechaInicioLanzaExcepcion() {
        PrestamoRequestDTO request = new PrestamoRequestDTO();
        request.setLibroId(1L);
        request.setFechaInicio(LocalDate.now());
        request.setFechaTermino(LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () -> {
            prestamoService.guardar(request);
        });

        verify(prestamoRepository, never()).save(any());
    }
}