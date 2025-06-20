package by.evgen.motor_depot.mapper;

import by.evgen.motor_depot.database.entity.Mechanic;
import by.evgen.motor_depot.dto.response.MechanicResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MechanicMapper {
    MechanicResponse toResponse(Mechanic mechanic);
}
