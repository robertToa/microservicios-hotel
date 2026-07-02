package com.util.svcreservations.service;

import com.util.svcreservations.dto.ReservationRequest;
import com.util.svcreservations.dto.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    ReservationResponse getReservationById(Long id);

    ReservationResponse createReservation(ReservationRequest reservationRequest);

    List<ReservationResponse> getReservationByGuestEmail(String guestEmail);

    ReservationResponse updateCheckOutDate(Long id, LocalDate checkOutDate);
}
