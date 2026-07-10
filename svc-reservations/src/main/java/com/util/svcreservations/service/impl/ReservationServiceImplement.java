package com.util.svcreservations.service.impl;

import com.util.svcreservations.client.RoomRestTemplate;
import com.util.svcreservations.client.RoomWebClient;
import com.util.svcreservations.dto.ReservationRequest;
import com.util.svcreservations.dto.ReservationResponse;
import com.util.svcreservations.dto.RoomAvailabilityResponse;
import com.util.svcreservations.dto.RoomResponse;
import com.util.svcreservations.exception.BusinessRuleException;
import com.util.svcreservations.exception.DuplicateResourceException;
import com.util.svcreservations.exception.ResourceNotFoundException;
import com.util.svcreservations.exception.RoomServiceException;
import com.util.svcreservations.mapper.ReservationMapper;
import com.util.svcreservations.model.Reservation;
import com.util.svcreservations.model.Status;
import com.util.svcreservations.repository.ReservationRepository;
import com.util.svcreservations.service.ReservationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImplement implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final RoomWebClient roomWebClient;
    private final RoomRestTemplate roomRestTemplate;

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "roomsService", fallbackMethod = "getReservationByIdFallback")
    @Retry(name = "roomsService")
    public ReservationResponse getReservationById(Long id){
        log.info("Obteniendo la reserva con id: {}", id);
        Reservation reservation =  reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservacion no enocontrado con el id: "+ id));
        RoomResponse room = roomRestTemplate.getRoomById(reservation.getRoomId());
        return reservationMapper.toResponseWithRoom(reservation, room);
    }

    public ReservationResponse getReservationByIdFallback(Long id, Throwable throwable){
        if(throwable instanceof ResourceNotFoundException){
            throw (ResourceNotFoundException) throwable;
        }
        Reservation reservation =  reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservacion no enocontrado con el id: "+ id));
        ReservationResponse reponse = reservationMapper.toResponse(reservation);
        reponse.setRoomNumber("Informacion del departamento temporal no disponible");
        reponse.setRoomType("Informacion del departamento temporal no disponible");
        return reponse;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "roomsService", fallbackMethod = "createReservationFallback")
    @Retry(name = "roomsService")
    public ReservationResponse createReservation(ReservationRequest reservationRequest){
        log.info("Nuevo reservacion con numero: {}", reservationRequest.getGuestName());
        if(reservationRepository.existsByRoomIdAndStatus(reservationRequest.getRoomId(), Status.ACTIVE)){
            throw new BusinessRuleException("Ya existe una reserva activa con el room id: " + reservationRequest.getRoomId());
        }
        log.info("Verificando disponibilidad del departamento via WebClient...");
        RoomAvailabilityResponse availabilityResponse = roomRestTemplate.getRoomAvailability(reservationRequest.getRoomId());
        if(!availabilityResponse.getAvailable()){
            throw new BusinessRuleException(
                    "Departamento con id: "+ reservationRequest.getRoomId() +
                            "No tiene cuartos disponible " + availabilityResponse.getAvailableRooms()
            );
        }
        validateCheckDate(reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate());
        Reservation reservation = reservationMapper.toEntity(reservationRequest);
        Reservation saveReservation = reservationRepository.save(reservation);
        log.info("Reservacion creado exitosamente con el id: {}", saveReservation.getId());
        RoomResponse room = roomWebClient.getRoomById(saveReservation.getRoomId());
        return reservationMapper.toResponseWithRoom(saveReservation, room);
    }

    public ReservationResponse createReservationFallback(ReservationRequest request, Throwable throwable){
        if(throwable instanceof BusinessRuleException){
            throw (BusinessRuleException) throwable;
        }
        throw new RoomServiceException(
                "Temporalmente el servicio de rooms no esta disponible. Por favo intente mas tarde "+
                        "Razon: " + throwable.getMessage()
        );
    }

    private void validateCheckDate(LocalDate checkInDate, LocalDate checkOutDate){
        if(checkInDate.isAfter(checkOutDate)){
            throw new BusinessRuleException("La fecha de entrada (" + checkInDate + ") no puede ser mayor a la fecha de salida (" + checkOutDate + ")" );
        }
    }

    @Override
    @Transactional
    public List<ReservationResponse> getReservationByGuestEmail(String guestEmail){
        log.info("Obteniendo todos los libros por email del huesped: {}", guestEmail);
        return reservationRepository.findByGuestEmail(guestEmail)
                .stream()
                .map(reservation -> {
                    try{
                        RoomResponse room = roomWebClient.getRoomById(reservation.getRoomId());
                        return reservationMapper.toResponseWithRoom(reservation, room);
                    }catch (Exception ex ){
                        return reservationMapper.toResponse(reservation);
                    }
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
        if(reservationRepository.existsByRoomIdAndStatus(reservation.getRoomId(), Status.COMPLETED) ||
                reservationRepository.existsByRoomIdAndStatus(reservation.getRoomId(), Status.CANCELLED)){
            throw new BusinessRuleException("La reserva no se encuentra activa: " + reservation.getRoomId());
        }
        reservation.setCheckOutDate(checkOutDate);
        reservation.setStatus(Status.COMPLETED);
        Reservation updateReservation = reservationRepository.save(reservation);
        log.info("Reservacion actualizado exitosamente para a reserva con id: {}", updateReservation.getId());
        RoomResponse room = roomRestTemplate.getRoomById(reservation.getRoomId());
        return reservationMapper.toResponseWithRoom(updateReservation, room);
    }
}
