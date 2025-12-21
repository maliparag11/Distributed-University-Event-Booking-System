package com.booking_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(RestClient.Builder builder,
                         @Value("${payment-service.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "notifyPaymentFallback")
    @Retry(name = "paymentService")
    public boolean notifyPaymentService(Long bookingId) {

        restClient.post()
                .uri("/payments")
                .body(Map.of("bookingId", bookingId))
                .retrieve()
                .toBodilessEntity();

        return true;
    }

    @SuppressWarnings("unused")
    private boolean notifyPaymentFallback(Long bookingId, Throwable throwable) {
        return false;
    }
}
