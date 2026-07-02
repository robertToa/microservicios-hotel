package com.util.svcrooms.dto;
import com.util.svcrooms.model.Type;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;

    private String roomNumber;

    private Type type;

    private double pricePerNight;

    private Integer totalCapacity;

    private Integer availableRooms;

    private Integer floor;

    private String description;

    private Boolean available;

}