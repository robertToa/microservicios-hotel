package com.util.svcrooms.dto;
import com.util.svcrooms.model.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    @NotBlank(message = "El numero del departamento es requerido")
    @Size(max = 10, message = "El numero del departamento no puede exceder de 10 caracteres")
    private String roomNumber;

    @NotNull(message = "El tipo de departamento es requerido")
    private Type type;

    @NotNull(message = "El precio del departamento es requerido")
    @Min(value = 0, message = "El precio debe ser igual o mayor a cero")
    private double pricePerNight;

    @NotNull(message = "La capacidad del departamento es requerido")
    @Min(value = 0, message = "La capacidad debe ser igual o mayor a cero")
    private Integer totalCapacity;

    @NotNull(message = "Los cuartos del departamento es requerido")
    @Min(value = 0, message = "Los cuartos disponibles debe ser igual o mayor a cero")
    private Integer availableRooms;

    @Min(value = 0, message = "El piso debe ser igual o mayor a cero")
    private Integer floor;

    @Size(max = 200, message = "La descripcion no puede exceder de 200 caracteres")
    private String description;
}
