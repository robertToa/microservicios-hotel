package com.util.svcreservations.controller;

import com.util.svcreservations.dto.ReservationRequest;
import com.util.svcreservations.dto.ReservationResponse;
import com.util.svcreservations.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id){
        log.info("GET /api/v1/reservations/{} - Obteniendo la reservacion por id", id);
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest reservationRequest){
        log.info("POST /api/v1/reservations - Creando la reservacion cpara el huesped: {}", reservationRequest.getGuestName());
        ReservationResponse createdReservation = reservationService.createReservation(reservationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @GetMapping("/guest/{email}")
    public ResponseEntity<List<ReservationResponse>> getReservationByGuestEmail(@PathVariable String email){
        log.info("GET /api/v1/reservations/guest/{} - Obteniendo reservacion por correo del huesped", email);
        return ResponseEntity.ok(reservationService.getReservationByGuestEmail(email));
    }

    @PatchMapping("/{id}/checkout")
    public ResponseEntity<ReservationResponse> updateCheckOutDate(@PathVariable Long id, @RequestParam LocalDate checkOutDate){
        log.info("PATH /api/v1/reservations/{}/checkout - Fecha de salida actualziada: {}", id, checkOutDate);
        return ResponseEntity.ok(reservationService.updateCheckOutDate(id, checkOutDate));
    }
}
