package by.evgen.motor_depot.database.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class TransportOrder {
    private Long id;
    private Consumer consumer;
    private Car car;
    private Driver driver;
    private BigDecimal cost;
    private String departurePlace;
    private String arrivalPlace;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private LocalDateTime createdAt;
    private Integer weight;
    private Integer width;
    private Integer height;
    private Integer length;
}
