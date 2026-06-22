package com.duoc.libros.service;

import com.duoc.libros.dto.LibroRequestDTO;
import com.duoc.libros.dto.LibroResponseDTO;
import com.duoc.libros.exception.ResourceNotFoundException;
import com.duoc.libros.model.Libro;
import com.duoc.libros.repository.LibroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    @Test
    void guardarLibroCorrectamente() {
        LibroRequestDTO request = new LibroRequestDTO();
        request.setTitulo("Clean Code");
        request.setAutor("Robert C. Martin");
        request.setEditorial("Prentice Hall");

        Libro libroGuardado = new Libro();
        libroGuardado.setId(1L);
        libroGuardado.setTitulo("Clean Code");
        libroGuardado.setAutor("Robert C. Martin");
        libroGuardado.setEditorial("Prentice Hall");

        when(libroRepository.save(any(Libro.class))).thenReturn(libroGuardado);

        LibroResponseDTO resultado = libroService.guardar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Clean Code", resultado.getTitulo());
        assertEquals("Robert C. Martin", resultado.getAutor());
        assertEquals("Prentice Hall", resultado.getEditorial());

        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    void obtenerLibroPorIdInexistenteLanzaExcepcion() {
        Long id = 99L;

        when(libroRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            libroService.obtenerPorId(id);
        });

        verify(libroRepository, times(1)).findById(id);
    }

    @Test
    void eliminarLibroInexistenteLanzaExcepcion() {
        Long id = 99L;

        when(libroRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            libroService.eliminar(id);
        });

        verify(libroRepository, times(1)).existsById(id);
        verify(libroRepository, never()).deleteById(id);
    }
}