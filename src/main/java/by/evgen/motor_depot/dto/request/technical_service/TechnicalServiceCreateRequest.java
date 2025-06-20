package by.evgen.motor_depot.dto.request.technical_service;

import java.time.LocalDateTime;

public record TechnicalServiceCreateRequest(
        Long mechanicId,
        Long carId,
        LocalDateTime serviceStartTime,
        LocalDateTime serviceEndTime,
        String description
) {
}
