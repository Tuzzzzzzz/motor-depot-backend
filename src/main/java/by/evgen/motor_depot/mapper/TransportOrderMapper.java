package by.evgen.motor_depot.mapper;

import by.evgen.motor_depot.database.entity.TransportOrder;
import by.evgen.motor_depot.dto.response.TransportOrderResponse;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {CarMapper.class, DriverMapper.class, ConsumerMapper.class}
)
public interface TransportOrderMapper {
    TransportOrderResponse toResponse(TransportOrder transportOrder);
}
