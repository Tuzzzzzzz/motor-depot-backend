package by.evgen.motor_depot.database.entity;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class Car {
    private Long id;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer bodyWidth;
    private Integer bodyHeight;
    private Integer bodyLength;
    private Integer cargoCapacity;
}
