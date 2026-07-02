package com.util.svcreservations.dto;

import com.util.svcreservations.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;

    private Long roomId;

    private String guestName;

    private String guestEmail;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Status status;

    private Integer totalNights;

    private LocalDateTime createdAt;

    private String roomNumber;

    private String roomType;

    private double pricePerNight;
}
