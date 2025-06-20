package by.evgen.motor_depot.database.repository;

import by.evgen.motor_depot.database.entity.Mechanic;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MechanicRepository {
    private final JdbcTemplate jdbcTemplate;

    public Mechanic create(Mechanic mechanic) {
        Long id = jdbcTemplate.queryForObject(
                """
                        INSERT INTO mechanic (
                            firstname,
                            lastname,
                            surname,
                            email,
                            phone_number
                        )
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id""",
                Long.class,
                mechanic.getFirstname(),
                mechanic.getLastname(),
                mechanic.getSurname(),
                mechanic.getEmail(),
                mechanic.getPhoneNumber()
        );

        mechanic.setId(id);

        return mechanic;
    }

    public Optional<Mechanic> findById(Long id) {
        try {
            Mechanic mechanic = jdbcTemplate.queryForObject(
                    "SELECT * FROM mechanic WHERE id = ?",
                    (rs, _) -> toMechanic(rs),
                    id
            );
            return Optional.ofNullable(mechanic);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Page<Mechanic> findAll(Pageable pageable){

        List<Mechanic> mechanics = jdbcTemplate.query(
                "SELECT * FROM mechanic ORDER BY id LIMIT ? OFFSET ?",
                (rs, _) -> toMechanic(rs),
                pageable.getPageSize(),
                pageable.getOffset()
        );

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM mechanic",
                Integer.class
        );

        return new PageImpl<>(mechanics, pageable, total);
    }

    public void update(Mechanic mechanic) {
        jdbcTemplate.update("""
                        UPDATE mechanic
                        SET firstname = ?,
                        lastname = ?,
                        surname = ?,
                        email = ?,
                        phone_number = ?
                        WHERE id = ?""",
                mechanic.getFirstname(),
                mechanic.getLastname(),
                mechanic.getSurname(),
                mechanic.getEmail(),
                mechanic.getPhoneNumber(),
                mechanic.getId()
        );
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM mechanic WHERE id = ?", id);
    }

    private Mechanic toMechanic(ResultSet rs) throws SQLException {
        return Mechanic.builder()
                .id(rs.getLong("id"))
                .firstname(rs.getObject("firstname", String.class))
                .lastname(rs.getObject("lastname", String.class))
                .surname(rs.getObject("surname", String.class))
                .email(rs.getObject("email", String.class))
                .phoneNumber(rs.getObject("phone_number", String.class))
                .build();
    }

    public List<Mechanic> findAllForExport(){
        return jdbcTemplate.query(
                "SELECT * FROM mechanic ORDER BY id",
                (rs, _) -> toMechanic(rs)
        );
    }
}
