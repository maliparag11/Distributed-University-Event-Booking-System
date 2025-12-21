package com.booking_service.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiError {

    private String message;
    private int status;

    public ApiError(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
