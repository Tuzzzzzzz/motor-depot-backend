package by.evgen.motor_depot.database.repository;

import by.evgen.motor_depot.database.entity.Car;
import by.evgen.motor_depot.database.entity.CarDriver;
import by.evgen.motor_depot.database.entity.Driver;
import lombok.RequiredArgsConstructor;
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
public class CarDriverRepository {
    private final JdbcTemplate jdbcTemplate;

    public CarDriver create(CarDriver carDriver){
        jdbcTemplate.update(
                """
                        INSERT INTO car_driver (car_id, driver_id)
                        VALUES (?, ?)""",
                carDriver.getCar().getId(),
                carDriver.getDriver().getId()
        );

        return carDriver;
    }

    public Page<CarDriver> findByCargoParameters(
            Integer width,
            Integer height,
            Integer length,
            Integer weight,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            Pageable pageable
    ) {
        String sql = """
                SELECT
                    c.id AS c_id,
                    c.license_plate AS c_license_plate,
                    c.brand AS c_brand,
                    c.model AS c_model,
                    c.body_width AS c_body_width,
                    c.body_height AS c_body_height,
                    c.body_length AS c_body_length,
                    c.cargo_capacity AS c_cargo_capacity,
                    d.id AS d_id,
                    d.firstname AS d_firstname,
                    d.lastname AS d_lastname,
                    d.surname AS d_surname,
                    d.phone_number AS d_phone_number,
                    d.email AS d_email
                FROM car c
                JOIN car_driver cd on c.id = cd.car_id
                JOIN driver d on cd.driver_id = d.id
                WHERE c.body_width >= ?
                AND c.body_height >= ?
                AND c.body_length >= ?
                AND c.cargo_capacity >= ?
                     AND NOT EXISTS (
                         SELECT 1
                         FROM transport_order o
                         WHERE o.car_id = c.id
                         AND (o.departure_time, o.arrival_time) OVERLAPS (?, ?)
                     )
                     AND NOT EXISTS (
                         SELECT 1
                         FROM transport_order o
                         WHERE o.driver_id = d.id
                         AND (o.departure_time, o.arrival_time) OVERLAPS (?, ?)
                     )
                ORDER BY cd.car_id, cd.driver_id
                LIMIT ? OFFSET ?""";

        Integer total = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM car c
                JOIN car_driver cd on c.id = cd.car_id
                JOIN driver d on cd.driver_id = d.id
                WHERE c.body_width >= ?
                AND c.body_height >= ?
                AND c.body_length >= ?
                AND c.cargo_capacity >= ?
                        AND NOT EXISTS (
                             SELECT 1
                             FROM transport_order o
                             WHERE o.car_id = c.id
                             AND (o.departure_time, o.arrival_time) OVERLAPS (?, ?)
                        )
                        AND NOT EXISTS (
                             SELECT 1
                             FROM transport_order o
                             WHERE o.driver_id = d.id
                             AND (o.departure_time, o.arrival_time) OVERLAPS (?, ?)
                        )""",
                Integer.class,
                width,
                height,
                length,
                weight,
                departureTime,
                arrivalTime,
                departureTime,
                arrivalTime
        );

        List<CarDriver> carDriverList = jdbcTemplate.query(
                sql,
                (rs, _) -> {
                    return toCarDriver(rs);
                },
                width,
                height,
                length,
                weight,
                departureTime,
                arrivalTime,
                departureTime,
                arrivalTime,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        return new PageImpl<>(carDriverList, pageable, total);
    }

    public void deleteByCarIdAndDriverId(Long carId, Long driverId) {
        jdbcTemplate.update(
                "DELETE FROM car_driver WHERE car_id = ? AND driver_id = ?",
                carId,
                driverId
        );
    }

    private CarDriver toCarDriver(ResultSet rs) throws SQLException {
        Car car = Car.builder()
                .id(rs.getLong("c_id"))
                .licensePlate(rs.getObject("c_license_plate", String.class))
                .brand(rs.getObject("c_brand", String.class))
                .model(rs.getObject("c_model", String.class))
                .bodyWidth(rs.getObject("c_body_width", Integer.class))
                .bodyHeight(rs.getObject("c_body_height", Integer.class))
                .bodyLength(rs.getObject("c_body_length", Integer.class))
                .cargoCapacity(rs.getObject("c_cargo_capacity", Integer.class))
                .build();

        Driver driver = Driver.builder()
                .id(rs.getLong("d_id"))
                .firstname(rs.getObject("d_firstname", String.class))
                .lastname(rs.getObject("d_lastname", String.class))
                .surname(rs.getObject("d_surname", String.class))
                .phoneNumber(rs.getObject("d_phone_number", String.class))
                .email(rs.getObject("d_email", String.class))
                .build();

        return CarDriver.builder()
                .car(car)
                .driver(driver)
                .build();
    }

    public Page<CarDriver> findAll(Pageable pageable){
        String sql = """
                SELECT c.id AS c_id,
                    c.license_plate AS c_license_plate,
                    c.brand AS c_brand,
                    c.model AS c_model,
                    c.body_width AS c_body_width,
                    c.body_height AS c_body_height,
                    c.body_length AS c_body_length,
                    c.cargo_capacity AS c_cargo_capacity,
                    d.id AS d_id,
                    d.firstname AS d_firstname,
                    d.lastname AS d_lastname,
                    d.surname AS d_surname,
                    d.phone_number AS d_phone_number,
                    d.email AS d_email
                FROM car c
                JOIN car_driver cd on c.id = cd.car_id
                JOIN driver d on cd.driver_id = d.id
                ORDER BY cd.car_id, cd.driver_id
                LIMIT ? OFFSET ?""";

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM car_driver",
                Integer.class
        );

        List<CarDriver> carDriverList = jdbcTemplate.query(
                sql,
                (rs, _) -> {
                    return toCarDriver(rs);
                },
                pageable.getPageSize(),
                pageable.getOffset()
        );

        return new PageImpl<>(carDriverList, pageable, total);
    }

    public List<CarDriver> findAllForExport(){
        String sql = """
                SELECT c.id AS c_id,
                    c.license_plate AS c_license_plate,
                    c.brand AS c_brand,
                    c.model AS c_model,
                    c.body_width AS c_body_width,
                    c.body_height AS c_body_height,
                    c.body_length AS c_body_length,
                    c.cargo_capacity AS c_cargo_capacity,
                    d.id AS d_id,
                    d.firstname AS d_firstname,
                    d.lastname AS d_lastname,
                    d.surname AS d_surname,
                    d.phone_number AS d_phone_number,
                    d.email AS d_email
                FROM car c
                JOIN car_driver cd on c.id = cd.car_id
                JOIN driver d on cd.driver_id = d.id
                ORDER BY cd.car_id, cd.driver_id""";

        return jdbcTemplate.query(
                sql,
                (rs, _) -> toCarDriver(rs)
        );
    }

    public Boolean existsByCarIdAndDriverId(Long carId, Long driverId) {
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM car_driver WHERE car_id = ? AND driver_id = ?)",
                Boolean.class,
                carId,
                driverId
        );
    }
}
