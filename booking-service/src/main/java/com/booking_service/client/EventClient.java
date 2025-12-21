package com.booking_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class EventClient {

    private static final Logger log = LoggerFactory.getLogger(EventClient.class);

    private final RestClient restClient;

    public EventClient(RestClient.Builder builder,
                       @Value("${event-service.base-url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    @CircuitBreaker(name = "eventService", fallbackMethod = "reserveSeatFallback")
    @Retry(name = "eventService")
    public boolean reserveSeat(Long eventId) {

        try {
            restClient.post()
                    .uri("/events/{id}/reserve", eventId)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Seat reserved successfully for eventId={}", eventId);
            return true;

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                log.warn("No seats available for eventId={}", eventId);
                return false;
            }

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Event not found for eventId={}", eventId);
                return false;
            }

            log.error("Client error while reserving seat for eventId={}, status={}",
                    eventId, e.getStatusCode());
            return false;

        } catch (RestClientResponseException e) {
            log.error("Event service error while reserving seat for eventId={}", eventId, e);
            throw e;
        }
    }

    @SuppressWarnings("unused")
    private boolean reserveSeatFallback(Long eventId, Throwable throwable) {
        log.error("Fallback triggered for eventId={} due to {}", 
                  eventId, throwable.getMessage());
        return false;
    }
}
