package by.evgen.motor_depot.database.entity;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class TechnicalService {
    private Long id;
    private LocalDateTime serviceStartTime;
    private LocalDateTime serviceEndTime;
    private Car car;
    private Mechanic mechanic;
    private String description;
    private LocalDateTime reportCreatedAt;
}
