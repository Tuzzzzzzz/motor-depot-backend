package by.evgen.motor_depot.dto.request.car_driver;

import java.time.LocalDateTime;

public record OrderParamsRequest(
        Integer width,
        Integer height,
        Integer length,
        Integer weight,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
) {
}
