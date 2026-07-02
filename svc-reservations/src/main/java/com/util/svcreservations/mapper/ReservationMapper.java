package com.util.svcreservations.mapper;

import com.util.svcreservations.dto.ReservationRequest;
import com.util.svcreservations.dto.ReservationResponse;
import com.util.svcreservations.dto.RoomResponse;
import com.util.svcreservations.model.Reservation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ReservationMapper {
    public Reservation toEntity(ReservationRequest reservationRequest){
        Integer totalNight = (int) ChronoUnit.DAYS.between(reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate());
        return Reservation.builder()
                .roomId(reservationRequest.getRoomId())
                .guestName(reservationRequest.getGuestName())
                .guestEmail(reservationRequest.getGuestEmail())
                .checkInDate(reservationRequest.getCheckInDate())
                .checkOutDate(reservationRequest.getCheckOutDate())
                .status(reservationRequest.getStatus())
                .totalNights(totalNight)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ReservationResponse toResponse(Reservation reservation, RoomResponse roomResponse){
        return ReservationResponse.builder()
                .id(reservation.getId())
                .roomId(reservation.getRoomId())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .status(reservation.getStatus())
                .totalNights(reservation.getTotalNights())
                .createdAt(reservation.getCreatedAt())
                .roomNumber(roomResponse.getRoomNumber())
                .roomType(roomResponse.getRoomType())
                .pricePerNight(roomResponse.getPricePerNight())
                .build();
    }
}
