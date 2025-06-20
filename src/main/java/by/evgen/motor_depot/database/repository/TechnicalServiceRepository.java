package by.evgen.motor_depot.database.repository;

import by.evgen.motor_depot.database.entity.Car;
import by.evgen.motor_depot.database.entity.Mechanic;
import by.evgen.motor_depot.database.entity.TechnicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@RequiredArgsConstructor
public class TechnicalServiceRepository {
    private final JdbcTemplate jdbcTemplate;

    public TechnicalService create(TechnicalService ts){
        return jdbcTemplate.queryForObject(
                """
                        INSERT INTO technical_service (
                            service_start_time,
                            service_end_time,
                            car_id,
                            mechanic_id,
                            description
                        ) VALUES (?, ?, ?, ?, ?)
                        RETURNING id, report_created_at""",
                (rs, _) -> {
                    ts.setId(rs.getLong("id"));
                    ts.setReportCreatedAt(rs.getObject("report_created_at", LocalDateTime.class));
                    return ts;
                },
                ts.getServiceStartTime(),
                ts.getServiceEndTime(),
                ts.getCar().getId(),
                ts.getMechanic().getId(),
                ts.getDescription()
        );
    }

    public void update(TechnicalService ts){
        jdbcTemplate.update(
                """
                        UPDATE technical_service
                            SET service_start_time = ?,
                                service_end_time = ?,
                                description = ?
                        WHERE id = ?
                        """,
                ts.getServiceStartTime(),
                ts.getServiceEndTime(),
                ts.getDescription(),
                ts.getId()
        );
    }

    public Optional<TechnicalService> findById(Long id){
        try {
            TechnicalService ts = jdbcTemplate.queryForObject(
                    """
                           SELECT
                               ts.id as ts_id,
                               ts.service_start_time as ts_service_start_time,
                               ts.service_end_time as ts_service_end_time,
                               ts.description as ts_description,
                               ts.car_id as ts_car_id,
                               ts.mechanic_id as ts_mechanic_id,
                               ts.report_created_at as ts_report_created_at,
                               c.id as c_id,
                               c.brand as c_brand,
                               c.model as c_model,
                               c.license_plate as c_license_plate,
                               c.body_width as c_body_width,
                               c.body_height as c_body_height,
                               c.body_length as c_body_length,
                               c.cargo_capacity as c_cargo_capacity,
                               m.id as m_id,
                               m.firstname as m_firstname,
                               m.lastname as m_lastname,
                               m.surname as m_surname,
                               m.phone_number as m_phone_number,
                               m.email as m_email
                           FROM technical_service ts
                           JOIN car c ON ts.car_id = c.id
                           JOIN mechanic m ON ts.mechanic_id = m.id
                           WHERE ts.id = ?""",
                    (rs, _) -> toTechnicalService(rs),
                    id
            );
            return Optional.ofNullable(ts);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Page<TechnicalService> findAll(Pageable pageable){

        List<TechnicalService> tsList = jdbcTemplate.query(
                """
                         SELECT
                             ts.id as ts_id,
                             ts.service_start_time as ts_service_start_time,
                             ts.service_end_time as ts_service_end_time,
                             ts.description as ts_description,
                             ts.car_id as ts_car_id,
                             ts.mechanic_id as ts_mechanic_id,
                             ts.report_created_at as ts_report_created_at,
                             c.id as c_id,
                             c.brand as c_brand,
                             c.model as c_model,
                             c.license_plate as c_license_plate,
                             c.body_width as c_body_width,
                             c.body_height as c_body_height,
                             c.body_length as c_body_length,
                             c.cargo_capacity as c_cargo_capacity,
                             m.id as m_id,
                             m.firstname as m_firstname,
                             m.lastname as m_lastname,
                             m.surname as m_surname,
                             m.phone_number as m_phone_number,
                             m.email as m_email
                         FROM technical_service ts
                         JOIN car c ON ts.car_id = c.id
                         JOIN mechanic m ON ts.mechanic_id = m.id
                         ORDER BY ts.report_created_at DESC
                         LIMIT ? OFFSET ?""",
                (rs, _) -> {
                    return toTechnicalService(rs);
                },
                pageable.getPageSize(),
                pageable.getOffset()
        );

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM technical_service",
                Integer.class
        );

        return new PageImpl<>(tsList, pageable, total);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM technical_service WHERE id = ?", id);
    }

    public Page<TechnicalService> findByCarId(Pageable pageable, Long carId){

        List<TechnicalService> tsList = jdbcTemplate.query(
                """
                           SELECT
                               ts.id as ts_id,
                               ts.service_start_time as ts_service_start_time,
                               ts.service_end_time as ts_service_end_time,
                               ts.description as ts_description,
                               ts.car_id as ts_car_id,
                               ts.mechanic_id as ts_mechanic_id,
                               ts.report_created_at as ts_report_created_at,
                               c.id as c_id,
                               c.brand as c_brand,
                               c.model as c_model,
                               c.license_plate as c_license_plate,
                               c.body_width as c_body_width,
                               c.body_height as c_body_height,
                               c.body_length as c_body_length,
                               c.cargo_capacity as c_cargo_capacity,
                               m.id as m_id,
                               m.firstname as m_firstname,
                               m.lastname as m_lastname,
                               m.surname as m_surname,
                               m.phone_number as m_phone_number,
                               m.email as m_email
                           FROM technical_service ts
                           JOIN car c ON ts.car_id = c.id
                           JOIN mechanic m ON ts.mechanic_id = m.id
                           WHERE ts.car_id = ?
                           ORDER BY ts.report_created_at DESC
                           LIMIT ? OFFSET ?""",
                (rs, _) -> {
                    return toTechnicalService(rs);
                },
                carId,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        Integer total = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM technical_service
                        WHERE car_id = ?""",
                Integer.class,
                carId
        );

        return new PageImpl<>(tsList, pageable, total);
    }

    private TechnicalService toTechnicalService(ResultSet rs) throws SQLException {
        Car car = Car.builder()
                .id(rs.getLong("c_id"))
                .brand(rs.getObject("c_brand", String.class))
                .model(rs.getObject("c_model", String.class))
                .licensePlate(rs.getObject("c_license_plate", String.class))
                .bodyWidth(rs.getObject("c_body_width", Integer.class))
                .bodyHeight(rs.getObject("c_body_height", Integer.class))
                .bodyLength(rs.getObject("c_body_length", Integer.class))
                .cargoCapacity(rs.getObject("c_cargo_capacity", Integer.class))
                .build();

        Mechanic mechanic = Mechanic.builder()
                .id(rs.getLong("m_id"))
                .firstname(rs.getObject("m_firstname", String.class))
                .lastname(rs.getObject("m_lastname", String.class))
                .surname(rs.getObject("m_surname", String.class))
                .phoneNumber(rs.getObject("m_phone_number", String.class))
                .email(rs.getObject("m_email", String.class))
                .build();

        return TechnicalService.builder()
                .id(rs.getLong("ts_id"))
                .car(car)
                .mechanic(mechanic)
                .serviceStartTime(rs.getObject("ts_service_start_time", LocalDateTime.class))
                .serviceEndTime(rs.getObject("ts_service_end_time", LocalDateTime.class))
                .description(rs.getObject("ts_description", String.class))
                .reportCreatedAt(rs.getObject("ts_report_created_at", LocalDateTime.class))
                .build();
    }

    public List<TechnicalService> findAllForExport() {
        return jdbcTemplate.query(
                """
                        SELECT
                            ts.id as ts_id,
                               ts.service_start_time as ts_service_start_time,
                               ts.service_end_time as ts_service_end_time,
                               ts.description as ts_description,
                               ts.car_id as ts_car_id,
                               ts.mechanic_id as ts_mechanic_id,
                               ts.report_created_at as ts_report_created_at,
                               c.id as c_id,
                               c.brand as c_brand,
                               c.model as c_model,
                               c.license_plate as c_license_plate,
                               c.body_width as c_body_width,
                               c.body_height as c_body_height,
                               c.body_length as c_body_length,
                               c.cargo_capacity as c_cargo_capacity,
                               m.id as m_id,
                               m.firstname as m_firstname,
                               m.lastname as m_lastname,
                               m.surname as m_surname,
                               m.phone_number as m_phone_number,
                               m.email as m_email
                        FROM technical_service ts
                        JOIN car c ON ts.car_id = c.id
                        JOIN mechanic m ON ts.mechanic_id = m.id
                        ORDER BY ts.report_created_at DESC""",
                (rs, _) -> toTechnicalService(rs)
        );
    }

    public Page<TechnicalService> findByMechanicId(Long mechanicId, Pageable pageable) {

        List<TechnicalService> tsList = jdbcTemplate.query(
                """
                           SELECT
                               ts.id as ts_id,
                               ts.service_start_time as ts_service_start_time,
                               ts.service_end_time as ts_service_end_time,
                               ts.description as ts_description,
                               ts.car_id as ts_car_id,
                               ts.mechanic_id as ts_mechanic_id,
                               ts.report_created_at as ts_report_created_at,
                               c.id as c_id,
                               c.brand as c_brand,
                               c.model as c_model,
                               c.license_plate as c_license_plate,
                               c.body_width as c_body_width,
                               c.body_height as c_body_height,
                               c.body_length as c_body_length,
                               c.cargo_capacity as c_cargo_capacity,
                               m.id as m_id,
                               m.firstname as m_firstname,
                               m.lastname as m_lastname,
                               m.surname as m_surname,
                               m.phone_number as m_phone_number,
                               m.email as m_email
                           FROM technical_service ts
                           JOIN car c ON ts.car_id = c.id
                           JOIN mechanic m ON ts.mechanic_id = m.id
                           WHERE ts.mechanic_id = ?
                           ORDER BY ts.report_created_at DESC
                           LIMIT ? OFFSET ?""",
                (rs, _) -> {
                    return toTechnicalService(rs);
                },
                mechanicId,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        Integer total = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM technical_service
                        WHERE mechanic_id = ?""",
                Integer.class,
                mechanicId
        );

        return new PageImpl<>(tsList, pageable, total);
    }
}
