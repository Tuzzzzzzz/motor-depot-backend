package by.evgen.motor_depot.database.repository;

import by.evgen.motor_depot.database.entity.Driver;
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
public class DriverRepository {
    private final JdbcTemplate jdbcTemplate;

    public Driver create(Driver driver) {
        Long id = jdbcTemplate.queryForObject(
                """
                        INSERT INTO driver (
                            firstname,
                            lastname,
                            surname,
                            email,
                            phone_number
                        )
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id""",
                Long.class,
                driver.getFirstname(),
                driver.getLastname(),
                driver.getSurname(),
                driver.getEmail(),
                driver.getPhoneNumber()
        );

        driver.setId(id);

        return driver;
    }

    public Optional<Driver> findById(Long id) {
        try {
            Driver driver = jdbcTemplate.queryForObject(
                    "SELECT * FROM driver WHERE id = ?",
                    (rs, rowNum) -> toDriver(rs),
                    id
            );
            return Optional.ofNullable(driver);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Page<Driver> findAll(Pageable pageable){

        List<Driver> drivers = jdbcTemplate.query(
                "SELECT * FROM driver ORDER BY id LIMIT ? OFFSET ?",
                (rs, rowNum) -> toDriver(rs),
                pageable.getPageSize(),
                pageable.getOffset()
        );

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM driver",
                Integer.class
        );

        return new PageImpl<>(drivers, pageable, total);
    }

    public void update(Driver driver) {
        jdbcTemplate.update("""
                        UPDATE driver
                        SET firstname = ?,
                        lastname = ?,
                        surname = ?,
                        email = ?,
                        phone_number = ?
                        WHERE id = ?""",
                driver.getFirstname(),
                driver.getLastname(),
                driver.getSurname(),
                driver.getEmail(),
                driver.getPhoneNumber(),
                driver.getId()
        );
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM driver WHERE id = ?", id);
    }

    private Driver toDriver(ResultSet rs) throws SQLException {
        return Driver.builder()
                .id(rs.getLong("id"))
                .firstname(rs.getObject("firstname", String.class))
                .lastname(rs.getObject("lastname", String.class))
                .surname(rs.getObject("surname", String.class))
                .email(rs.getObject("email", String.class))
                .phoneNumber(rs.getObject("phone_number", String.class))
                .build();
    }

    public List<Driver> findAllForExport(){
        return jdbcTemplate.query(
                "SELECT * FROM driver ORDER BY id",
                (rs, rowNum) -> toDriver(rs)
        );
    }
}
