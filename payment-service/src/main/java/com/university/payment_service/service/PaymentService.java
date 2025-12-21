package com.university.payment_service.service;

import com.university.payment_service.model.Payment;
import com.university.payment_service.model.PaymentStatus;
import com.university.payment_service.repo.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentReference(UUID.randomUUID().toString());
        payment.setProcessedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public Payment completePayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public Payment failPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setProcessedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
}
