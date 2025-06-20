package by.evgen.motor_depot.dto.response;

public record MechanicResponse(
        Long id,
        String firstname,
        String lastname,
        String surname,
        String phoneNumber,
        String email
) {
}
