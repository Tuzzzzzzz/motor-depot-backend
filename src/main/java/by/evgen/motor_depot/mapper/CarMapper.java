package by.evgen.motor_depot.mapper;

import by.evgen.motor_depot.database.entity.Car;
import by.evgen.motor_depot.dto.response.CarResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarResponse toResponse(Car car);
}
