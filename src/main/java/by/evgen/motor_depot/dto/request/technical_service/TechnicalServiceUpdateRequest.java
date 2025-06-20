package by.evgen.motor_depot.dto.request.technical_service;

import java.time.LocalDateTime;

public record TechnicalServiceUpdateRequest(
        LocalDateTime serviceStartTime,
        LocalDateTime serviceEndTime,
        String description
) {
}
