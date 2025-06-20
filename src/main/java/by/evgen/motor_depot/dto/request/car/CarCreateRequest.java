package by.evgen.motor_depot.dto.request.car;

public record CarCreateRequest(
        String licensePlate,
        String brand,
        String model,
        Integer bodyWidth,
        Integer bodyHeight,
        Integer bodyLength,
        Integer cargoCapacity
) {
}
