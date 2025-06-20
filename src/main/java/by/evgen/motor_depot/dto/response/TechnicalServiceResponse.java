package by.evgen.motor_depot.dto.response;


import java.time.LocalDateTime;

public record TechnicalServiceResponse(
        Long id,
        LocalDateTime serviceStartTime,
        LocalDateTime serviceEndTime,
        CarResponse car,
        MechanicResponse mechanic,
        String description,
        LocalDateTime reportCreatedAt
) {
}
