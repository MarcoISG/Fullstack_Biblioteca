package com.duoc.prestamos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_inicio", nullable = false)
    @NotNull(message = "La fecha de inicio del préstamo es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a hoy")
    private LocalDate fechaInicio;

    @Column(name = "fecha_termino", nullable = false)
    @NotNull(message = "La fecha de término del préstamo es obligatoria")
    @FutureOrPresent(message = "La fecha de término no puede ser anterior a hoy")
    private LocalDate fechaTermino;

    @Column(name = "libro_id", nullable = false)
    @NotNull(message = "El ID del libro es obligatorio")
    @Positive(message = "El ID del libro debe ser mayor que cero")
    private Long libroId;
}
