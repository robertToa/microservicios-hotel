package com.util.svcreservations.client;

import com.util.svcreservations.dto.RoomAvailabilityResponse;
import com.util.svcreservations.dto.RoomResponse;
import com.util.svcreservations.exception.ResourceNotFoundException;
import com.util.svcreservations.exception.RoomServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class RoomRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${rooms.service.url}")
    private String roomServiceUrl;

    public RoomRestTemplate(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public RoomAvailabilityResponse getRoomAvailability(Long roomId){
        String url = roomServiceUrl + "/api/v1/rooms/" + roomId + "/availability";
        log.info("RestTemplate - Llamando rooms-service: GET {}", url);
        try{
            ResponseEntity<RoomAvailabilityResponse> response = restTemplate.getForEntity(
                    url,
                    RoomAvailabilityResponse.class
            );
            log.info("RestTemplate - Estado de la respuesta : {}", response.getStatusCode());
            return response.getBody();
        }catch (HttpClientErrorException ex){
            if(ex.getStatusCode() == HttpStatus.NOT_FOUND){
                throw new ResourceNotFoundException("Departamento no encontrado en el rooms-service con id: "+ roomId);
            }
            throw new RoomServiceException("Error del cliente al llamar el rooms-service: "+  ex.getMessage());
        }
        catch (ResourceAccessException ex){
            log.error("RestTemplate - No se logro conectar con rooms-service: {}", ex.getMessage());
            throw new RoomServiceException("No se logro conectar a rooms-service: "+  ex.getMessage(), ex);
        }
    }

    public RoomResponse getRoomById(Long roomId){
        String url = roomServiceUrl + "/api/v1/rooms/" + roomId ;
        log.info("RestTemplate - Llamando rooms-service: GET {}", url);
        try{
            ResponseEntity<RoomResponse> response = restTemplate.getForEntity(
                    url,
                    RoomResponse.class
            );
            log.info("RestTemplate - Estado de la respuesta : {}", response.getStatusCode());
            return response.getBody();
        }catch (HttpClientErrorException ex){
            if(ex.getStatusCode() == HttpStatus.NOT_FOUND){
                throw new ResourceNotFoundException("Departamento no encontrado en el rooms-service con id: "+ roomId);
            }
            throw new RoomServiceException("Error del cliente al llamar el rooms-service: "+  ex.getMessage());
        }
        catch (ResourceAccessException ex){
            log.error("RestTemplate - No se logro conectar con rooms-service: {}", ex.getMessage());
            throw new RoomServiceException("No se logro conectar a rooms-service: "+  ex.getMessage(), ex);
        }
    }
}
