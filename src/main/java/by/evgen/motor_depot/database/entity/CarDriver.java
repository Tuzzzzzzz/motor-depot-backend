package by.evgen.motor_depot.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CarDriver {
    private Car car;
    private Driver driver;
}
