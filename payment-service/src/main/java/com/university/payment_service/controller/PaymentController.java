package com.university.payment_service.controller;

import com.university.payment_service.model.Payment;
import com.university.payment_service.model.PaymentStatus;
import com.university.payment_service.repo.PaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    private static final String BOOKING_SERVICE_URL =
            "http://booking-service:8082/bookings/";

    public PaymentController(PaymentRepository paymentRepository,
                             RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {

        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentReference(UUID.randomUUID().toString());
        payment.setProcessedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        return ResponseEntity.status(201).body(savedPayment);
    }

    @PutMapping("/{paymentId}/complete")
    public ResponseEntity<Payment> completePayment(@PathVariable Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            return ResponseEntity.badRequest().body(payment);
        }

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        restTemplate.postForObject(
                BOOKING_SERVICE_URL + payment.getBookingId() + "/confirm",
                null,
                Void.class
        );

        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{paymentId}/fail")
    public ResponseEntity<Payment> failPayment(@PathVariable Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setProcessedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        restTemplate.postForObject(
                BOOKING_SERVICE_URL + payment.getBookingId() + "/cancel",
                null,
                Void.class
        );

        return ResponseEntity.ok(payment);
    }
}
