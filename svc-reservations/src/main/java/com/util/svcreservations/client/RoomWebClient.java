package com.util.svcreservations.client;

import com.util.svcreservations.dto.RoomAvailabilityResponse;
import com.util.svcreservations.dto.RoomResponse;
import com.util.svcreservations.exception.ResourceNotFoundException;
import com.util.svcreservations.exception.RoomServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Slf4j
public class RoomWebClient {
    private final WebClient webClient;

    public RoomWebClient(WebClient webClient){
        this.webClient = webClient;
    }

    public RoomAvailabilityResponse getRoomAvailability(long roomId){
        log.info("WebClient - Llamando rooms-service: GET {/api/v1/rooms/{}/availability}", roomId);
        try{
            return webClient
                    .get()
                    .uri("/api/v1/rooms/{roomId}/availability", roomId)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            response -> response.bodyToMono(String.class)
                                    .map( body -> new ResourceNotFoundException(
                                            "Departamenta no encontrado en el rooms-service con id: " + roomId))
                    )
                    .onStatus(
                            status -> status.is4xxClientError(),
                            response -> response.bodyToMono(String.class)
                                    .map( body -> new RoomServiceException(
                                            "Error de cliente desde el rooms-service: " + body))
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map( body -> new RoomServiceException(
                                            "Error del servidor desde el rooms-service: " + body))
                    )
                    .bodyToMono(RoomAvailabilityResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClient -Error HHTP desde rooms-service: {} {}",  ex.getStatusCode(), ex.getMessage());
            throw new RoomServiceException("Error al llamar al rooms-service: "+ ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("WebClient - No se logro conectar con rooms-service: {} ", ex.getMessage());
            throw new RoomServiceException("No se logro conectar con rooms-service: "+ ex.getMessage(), ex);
        }
    }

    public RoomResponse getRoomById(long roomId){
        log.info("WebClient - Llamando rooms-service: GET {/api/v1/rooms/{}}", roomId);
        try{
            return webClient
                    .get()
                    .uri("/api/v1/rooms/{roomId}", roomId)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            response -> response.bodyToMono(String.class)
                                    .map( body -> new ResourceNotFoundException(
                                            "Libro no encontrado en el rooms-service con id: " + roomId))
                    )
                    .onStatus(
                            status -> status.is4xxClientError(),
                            response -> response.bodyToMono(String.class)
                                    .map( body -> new RoomServiceException(
                                            "Error de cliente desde el rooms-service: " + body))
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .map( body -> new RoomServiceException(
                                            "Error del servidor desde el rooms-service: " + body))
                    )
                    .bodyToMono(RoomResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClient -Error HHTP desde rooms-service: {} {}",  ex.getStatusCode(), ex.getMessage());
            throw new RoomServiceException("Error al llamar al rooms-service: "+ ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("WebClient - No se logro conectar con rooms-service: {} ", ex.getMessage());
            throw new RoomServiceException("No se logro conectar con rooms-service: "+ ex.getMessage(), ex);
        }
    }
}
