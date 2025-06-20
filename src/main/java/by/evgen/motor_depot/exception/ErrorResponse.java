package by.evgen.motor_depot.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String status,
        String message,
        LocalDateTime timestamp
) {}