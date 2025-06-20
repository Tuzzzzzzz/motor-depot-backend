package by.evgen.motor_depot.dto.response;

public record CarResponse(
        Long id,
        String licensePlate,
        String brand,
        String model,
        Integer bodyWidth,
        Integer bodyHeight,
        Integer bodyLength,
        Integer cargoCapacity
) {
}
