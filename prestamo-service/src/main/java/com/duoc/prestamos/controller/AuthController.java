package com.duoc.prestamos.controller;

import com.duoc.prestamos.dto.ErrorResponseDTO;
import com.duoc.prestamos.dto.LoginRequestDTO;
import com.duoc.prestamos.dto.LoginResponseDTO;
import com.duoc.prestamos.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Operaciones de autenticación con JWT")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Operation(summary = "Iniciar sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Intento de login para usuario {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDTO(token, "Bearer", userDetails.getUsername()));
    }
}
