package com.util.svcreservations.repository;

import com.util.svcreservations.model.Reservation;
import com.util.svcreservations.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByRoomIdAndStatus(Long roomId, Status status);

    List<Reservation> findByGuestEmail(String guestEmail);
}
