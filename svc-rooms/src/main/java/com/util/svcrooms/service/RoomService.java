package com.util.svcrooms.service;

import com.util.svcrooms.dto.AvailabilityResponse;
import com.util.svcrooms.dto.RoomRequest;
import com.util.svcrooms.dto.RoomResponse;

import java.util.List;

public interface RoomService {
    List<RoomResponse> getAllRooms();

    RoomResponse getRoomById(Long id);

    RoomResponse createRoom(RoomRequest roomRequest);

    AvailabilityResponse getRoomAvailability(Long id);

    RoomResponse updateAvailability(Long id, Integer availableRooms);

}
