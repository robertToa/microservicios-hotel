package com.util.svcreservations.service.impl;

import com.util.svcreservations.dto.ReservationRequest;
import com.util.svcreservations.dto.ReservationResponse;
import com.util.svcreservations.dto.RoomResponse;
import com.util.svcreservations.exception.BusinessRuleException;
import com.util.svcreservations.exception.DuplicateResourceException;
import com.util.svcreservations.exception.ResourceNotFoundException;
import com.util.svcreservations.mapper.ReservationMapper;
import com.util.svcreservations.model.Reservation;
import com.util.svcreservations.model.Status;
import com.util.svcreservations.repository.ReservationRepository;
import com.util.svcreservations.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImplement implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id){
        log.info("Obteniendo la reserva con id: {}", id);
        Reservation reservation =  reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservacion no enocontrado con el id: "+ id));
        return reservationMapper.toResponse(reservation, new RoomResponse());
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest reservationRequest){
        log.info("Nuevo reservacion con numero: {}", reservationRequest.getGuestName());
        if(reservationRepository.existsByRoomIdAndStatus(reservationRequest.getRoomId(), Status.ACTIVE)){
            throw new DuplicateResourceException("Ya existe una reserva activa con el room id: " + reservationRequest.getRoomId());
        }
        validateCheckDate(reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate());
        Reservation reservation = reservationMapper.toEntity(reservationRequest);
        Reservation saveReservation = reservationRepository.save(reservation);
        log.info("Reservacion creado exitosamente con el id: {}", saveReservation.getId());
        return reservationMapper.toResponse(saveReservation, new RoomResponse());
    }

    private void validateCheckDate(LocalDate checkInDate, LocalDate checkOutDate){
        if(checkInDate.isAfter(checkOutDate)){
            throw new BusinessRuleException("La fecha de entrada (" + checkInDate + ") no puede ser mayor a la fecha de salida (" + checkOutDate + ")");
        }
    }

    @Override
    @Transactional
    public List<ReservationResponse> getReservationByGuestEmail(String guestEmail){
        log.info("Obteniendo todos los libros por email del huesped: {}", guestEmail);
        return reservationRepository.findByGuestEmail(guestEmail)
                .stream()
                .map(reservation -> {
                    return reservationMapper.toResponse(reservation, new RoomResponse());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse updateCheckOutDate(Long id, LocalDate checkOutDate){
        log.info("Actualizacion fecha de salida de la reservacion id: {} , fecha de salida {}", id, checkOutDate);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservacion no enocontrado con id: " + id));
        validateCheckDate(reservation.getCheckInDate(), checkOutDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setStatus(Status.COMPLETED);
        Reservation updateReservation = reservationRepository.save(reservation);
        log.info("Reservacion actualizado exitosamente para a reserva con id: {}", updateReservation.getId());
        return reservationMapper.toResponse(updateReservation, new RoomResponse());
    }
}
