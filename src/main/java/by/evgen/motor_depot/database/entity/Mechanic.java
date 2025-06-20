package by.evgen.motor_depot.database.entity;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class Mechanic {
    private Long id;
    private String firstname;
    private String lastname;
    private String surname;
    private String phoneNumber;
    private String email;
}
