package by.evgen.motor_depot.dto.response;

public record DriverResponse(
        Long id,
        String firstname,
        String lastname,
        String surname,
        String phoneNumber,
        String email
) {
}
