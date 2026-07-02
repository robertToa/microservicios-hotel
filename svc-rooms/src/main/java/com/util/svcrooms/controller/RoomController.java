package com.util.svcrooms.controller;

import com.util.svcrooms.dto.AvailabilityResponse;
import com.util.svcrooms.dto.RoomRequest;
import com.util.svcrooms.dto.RoomResponse;
import com.util.svcrooms.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms(){
        log.info("GET /api/v1/rooms - Obteniendo todos los departamentos");
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id){
        log.info("GET /api/v1/rooms/{} - Obteniendo el departamento por id", id);
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest roomRequest){
        log.info("POST /api/v1/rooms - Creando el departamento con numero: {}", roomRequest.getRoomNumber());
        RoomResponse createdRoom = roomService.createRoom(roomRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> getRoomAvailability(@PathVariable Long id){
        log.info("GET /api/v1/rooms/{}/availability - Verificando disponibilidad", id);
        return ResponseEntity.ok(roomService.getRoomAvailability(id));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<RoomResponse> updateAvailability(@PathVariable Long id, @RequestParam @Min(value = 0, message = "Cuartos disponible igual o mayor a cero") Integer availableRooms){
        log.info("PATH /api/v1/rooms/{}/availability - Nuevo cuartos disponible: {}", id, availableRooms);
        return ResponseEntity.ok(roomService.updateAvailability(id, availableRooms));
    }

}
