package com.duoc.prestamos.client;

import com.duoc.prestamos.dto.LibroResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "libro-service", url = "${libro-service.url}")
public interface LibroClient {

    @GetMapping("/api/v1/libros/{id}")
    LibroResponseDTO obtenerPorId(@PathVariable Long id);
}
