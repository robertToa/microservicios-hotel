package com.util.svcreservations.dto;

import com.util.svcreservations.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotNull(message = "Departamento es requerido")
    private Long roomId;

    @NotBlank(message = "Huesped es requerido")
    @Size(max = 100, message = "Nombre del huesped no puede exceder de 100 caracteres")
    private String guestName;

    @NotBlank(message = "Correo del huesped es requerido")
    @Size(max = 200, message = "El correo del huesped no puede exceder de 200 caracteres")
    private String guestEmail;

    @NotNull(message = "Fecha de entrada es requerido")
    private LocalDate checkInDate;

    @NotNull(message = "Fecha de salida es requerido")
    private LocalDate checkOutDate;

    @NotNull(message = "Estatus es requerido")
    private Status status;
}
