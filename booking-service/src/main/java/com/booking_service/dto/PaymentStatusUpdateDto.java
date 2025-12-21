package com.booking_service.dto;

import com.booking_service.entity.PaymentStatus;
import lombok.Data;

@Data
public class PaymentStatusUpdateDto {
    private PaymentStatus status;
}
