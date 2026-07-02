package com.util.svcrooms.mapper;

import com.util.svcrooms.dto.AvailabilityResponse;
import com.util.svcrooms.dto.RoomRequest;
import com.util.svcrooms.dto.RoomResponse;
import com.util.svcrooms.model.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {
    public Room toEntity(RoomRequest roomRequest){
        return Room.builder()
                .roomNumber(roomRequest.getRoomNumber())
                .type(roomRequest.getType())
                .pricePerNight(roomRequest.getPricePerNight())
                .totalCapacity(roomRequest.getTotalCapacity())
                .availableRooms(roomRequest.getAvailableRooms())
                .floor(roomRequest.getFloor())
                .description(roomRequest.getDescription())
                .build();
    }

    public RoomResponse toResponse(Room room){
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType())
                .pricePerNight(room.getPricePerNight())
                .totalCapacity(room.getTotalCapacity())
                .availableRooms(room.getAvailableRooms())
                .floor(room.getFloor())
                .description(room.getDescription())
                .available(room.getAvailableRooms() > 0)
                .build();
    }

    public AvailabilityResponse toAvailabilityResponse(Room room) {
        return AvailabilityResponse.builder()
                .roomId(room.getId())
                .available(room.getAvailableRooms() > 0)
                .availableRooms(room.getAvailableRooms())
                .build();
    }
}
