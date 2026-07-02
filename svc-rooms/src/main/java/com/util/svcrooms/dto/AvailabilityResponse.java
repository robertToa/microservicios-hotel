package com.util.svcrooms.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    private Long roomId;

    private boolean available;

    private Integer availableRooms;
}
