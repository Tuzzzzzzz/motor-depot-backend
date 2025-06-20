package by.evgen.motor_depot.mapper;

import by.evgen.motor_depot.database.entity.Driver;
import by.evgen.motor_depot.dto.response.DriverResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    DriverResponse toResponse(Driver driver);
}
