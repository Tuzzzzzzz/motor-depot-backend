package by.evgen.motor_depot.mapper;

import by.evgen.motor_depot.database.entity.TechnicalService;
import by.evgen.motor_depot.dto.response.TechnicalServiceResponse;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {CarMapper.class, MechanicMapper.class}
)
public interface TechnicalServiceMapper {
    TechnicalServiceResponse toResponse(TechnicalService ts);
}
