package com.util.svcrooms.service.impl;

import com.util.svcrooms.dto.AvailabilityResponse;
import com.util.svcrooms.dto.RoomRequest;
import com.util.svcrooms.dto.RoomResponse;
import com.util.svcrooms.exception.BusinessRuleException;
import com.util.svcrooms.exception.DuplicateResourceException;
import com.util.svcrooms.exception.ResourceNotFoundException;
import com.util.svcrooms.mapper.RoomMapper;
import com.util.svcrooms.model.Room;
import com.util.svcrooms.repository.RoomRepository;
import com.util.svcrooms.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImplement implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms(){
        log.info("Obteniendo todos los departamentos");
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomResponse getRoomById(Long id){
        log.info("Obteniendo el departamento con id: {}", id);
        Room room =  roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no enocontrado con el id: "+ id));
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest roomRequest){
        log.info("Nuevo departamento con numero: {}", roomRequest.getRoomNumber());
        if(roomRepository.existsByRoomNumber(roomRequest.getRoomNumber())){
            throw new DuplicateResourceException("Ya existe un departamento con el numero: " + roomRequest.getRoomNumber());
        }
        validateStock(roomRequest.getAvailableRooms(), roomRequest.getTotalCapacity());
        Room room = roomMapper.toEntity(roomRequest);
        Room saveRoom = roomRepository.save(room);
        log.info("Departamento creado exitosamente con el id: {}", saveRoom.getId());
        return roomMapper.toResponse(saveRoom);
    }

    private void validateStock(Integer availableRooms, Integer totalCapacity){
        if(availableRooms > totalCapacity){
            throw new BusinessRuleException("Existe disponibles (" + availableRooms + ") no puede exceder la capacidad total (" + totalCapacity + ")");
        }
    }

    @Override
    @Transactional
    public AvailabilityResponse getRoomAvailability(Long id){
        log.info("Verificando cuartos disponible del departamento con id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con id: " + id));
        return roomMapper.toAvailabilityResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse updateAvailability(Long id, Integer availableRooms){
        log.info("Actualizacion disponibilidad de los cuartos para el departamento con id: {} , nuevo cuartos disponible {}", id, availableRooms);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con id: " + id));
        validateStock(availableRooms, room.getTotalCapacity());
        room.setAvailableRooms(availableRooms);
        Room updateRoom = roomRepository.save(room);
        log.info("Cuartos disponible actualizado exitosamente para el departamento con id: {}", updateRoom.getId());
        return roomMapper.toResponse(updateRoom);
    }

}
