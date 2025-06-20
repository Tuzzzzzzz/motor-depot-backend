package by.evgen.motor_depot.mapper;

import by.evgen.motor_depot.database.entity.Consumer;
import by.evgen.motor_depot.dto.response.ConsumerResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsumerMapper {
    ConsumerResponse toResponse(Consumer consumer);
}
