package by.evgen.motor_depot.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransportOrderResponse(
        Long id,
        ConsumerResponse consumer,
        CarResponse car,
        DriverResponse driver,
        BigDecimal cost,
        String departurePlace,
        String arrivalPlace,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        LocalDateTime createdAt,
        Integer weight,
        Integer width,
        Integer height,
        Integer length
) {
}
