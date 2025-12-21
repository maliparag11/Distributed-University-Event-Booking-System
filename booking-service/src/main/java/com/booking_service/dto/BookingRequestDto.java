package com.booking_service.dto;

import lombok.Data;

@Data
public class BookingRequestDto {

    private Long userId;
    private Long eventId;
}
