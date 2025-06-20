package by.evgen.motor_depot.database.repository;

import by.evgen.motor_depot.database.entity.Consumer;
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
public class ConsumerRepository {
    private final JdbcTemplate jdbcTemplate;

    public Consumer create(Consumer consumer) {
        Long id = jdbcTemplate.queryForObject(
                """
                        INSERT INTO consumer (
                            firstname,
                            lastname,
                            surname,
                            email,
                            phone_number
                        )
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id""",
                Long.class,
                consumer.getFirstname(),
                consumer.getLastname(),
                consumer.getSurname(),
                consumer.getEmail(),
                consumer.getPhoneNumber()
        );

        consumer.setId(id);

        return consumer;
    }

    public Optional<Consumer> findById(Long id) {
        try {
            Consumer consumer = jdbcTemplate.queryForObject(
                    "SELECT * FROM consumer WHERE id = ?",
                    (rs, _) -> toConsumer(rs),
                    id
            );
            return Optional.ofNullable(consumer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Page<Consumer> findAll(Pageable pageable){

        List<Consumer> consumers = jdbcTemplate.query(
                "SELECT * FROM consumer ORDER BY id LIMIT ? OFFSET ?",
                (rs, _) -> toConsumer(rs),
                pageable.getPageSize(),
                pageable.getOffset()
        );

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM consumer",
                Integer.class
        );

        return new PageImpl<>(consumers, pageable, total);
    }

    public void update(Consumer consumer) {
        jdbcTemplate.update("""
                        UPDATE consumer
                        SET firstname = ?,
                        lastname = ?,
                        surname = ?,
                        email = ?,
                        phone_number = ?
                        WHERE id = ?""",
                consumer.getFirstname(),
                consumer.getLastname(),
                consumer.getSurname(),
                consumer.getEmail(),
                consumer.getPhoneNumber(),
                consumer.getId());
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM consumer WHERE id = ?", id);
    }

    private Consumer toConsumer(ResultSet rs) throws SQLException {
        return Consumer.builder()
                .id(rs.getLong("id"))
                .firstname(rs.getObject("firstname", String.class))
                .lastname(rs.getObject("lastname", String.class))
                .surname(rs.getObject("surname", String.class))
                .email(rs.getObject("email", String.class))
                .phoneNumber(rs.getObject("phone_number", String.class))
                .build();
    }

    public List<Consumer> findAllForExport(){
        return jdbcTemplate.query(
                "SELECT * FROM consumer ORDER BY id",
                (rs, _) -> toConsumer(rs)
        );
    }
}

