package by.evgen.motor_depot.dto.request;

public record ConsumerRequest(
        String firstname,
        String lastname,
        String surname,
        String phoneNumber,
        String email
) {
}
