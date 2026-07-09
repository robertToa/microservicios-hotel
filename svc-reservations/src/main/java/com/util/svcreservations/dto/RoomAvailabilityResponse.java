package com.util.svcreservations.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityResponse {
    private Long roomId;

    private Boolean available;

    private Integer availableRooms;
}
