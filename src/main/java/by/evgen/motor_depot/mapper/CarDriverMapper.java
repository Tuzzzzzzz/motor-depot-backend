package by.evgen.motor_depot.mapper;

import by.evgen.motor_depot.database.entity.CarDriver;
import by.evgen.motor_depot.dto.response.CarDriverResponse;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {CarMapper.class, DriverMapper.class}
)
public interface CarDriverMapper {
    CarDriverResponse toResponse(CarDriver carDriver);
}
