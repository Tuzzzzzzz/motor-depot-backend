package by.evgen.motor_depot.dto.response;

import by.evgen.motor_depot.database.entity.Car;
import by.evgen.motor_depot.database.entity.Driver;

public record CarDriverResponse(
        CarResponse car,
        DriverResponse driver
) {
}
