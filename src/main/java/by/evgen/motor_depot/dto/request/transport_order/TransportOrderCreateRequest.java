package by.evgen.motor_depot.dto.request.transport_order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransportOrderCreateRequest(
        Long consumerId,
        Long carId,
        Long driverId,
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
