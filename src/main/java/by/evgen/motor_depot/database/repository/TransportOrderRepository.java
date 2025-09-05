package by.evgen.motor_depot.database.repository;

import by.evgen.motor_depot.database.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransportOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransportOrder create(TransportOrder transportOrder){
        String sql = """
            INSERT INTO transport_order (
                consumer_id,
                car_id,
                driver_id,
                cost,
                departure_place,
                arrival_place,
                departure_time,
                arrival_time,
                weight,
                width,
                height,
                length
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id, created_at
            """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    transportOrder.setId(rs.getLong("id"));
                    transportOrder.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                    return transportOrder;
                },
                transportOrder.getConsumer().getId(),
                transportOrder.getCar().getId(),
                transportOrder.getDriver().getId(),
                transportOrder.getCost(),
                transportOrder.getDeparturePlace(),
                transportOrder.getArrivalPlace(),
                transportOrder.getDepartureTime(),
                transportOrder.getArrivalTime(),
                transportOrder.getWeight(),
                transportOrder.getWidth(),
                transportOrder.getHeight(),
                transportOrder.getLength()
        );
    }

    public void update(TransportOrder transportOrder){
        String sql = """
            UPDATE transport_order
                SET car_id = ?,
                    driver_id = ?,
                    cost = ?,
                    departure_place = ?,
                    arrival_place = ?,
                    departure_time = ?,
                    arrival_time = ?,
                    weight = ?,
                    width = ?,
                    height = ?,
                    length = ?
            WHERE id = ?
            """;

        jdbcTemplate.update(
                sql,
                transportOrder.getCar().getId(),
                transportOrder.getDriver().getId(),
                transportOrder.getCost(),
                transportOrder.getDeparturePlace(),
                transportOrder.getArrivalPlace(),
                transportOrder.getDepartureTime(),
                transportOrder.getArrivalTime(),
                transportOrder.getWeight(),
                transportOrder.getWidth(),
                transportOrder.getHeight(),
                transportOrder.getLength(),
                transportOrder.getId()
        );
    }

    public void delete(Long id){
        jdbcTemplate.update("DELETE FROM transport_order WHERE id = ?", id);
    }

    public Page<TransportOrder> findByCarId(Long carId, Pageable pageable){
        String sql = """
                SELECT
                    o.id as o_id,
                    o.cost as o_cost,
                    o.departure_place as o_departure_place,
                    o.arrival_place as o_arrival_place,
                    o.departure_time as o_departure_time,
                    o.arrival_time as o_arrival_time,
                    o.created_at as o_created_at,
                    o.weight as o_weight,
                    o.width as o_width,
                    o.height as o_height,
                    o.length as o_length,
                    cm.id as cm_id,
                    cm.firstname as cm_firstname,
                    cm.lastname as cm_lastname,
                    cm.surname as cm_surname,
                    cm.email as cm_email,
                    cm.phone_number as cm_phone_number,
                    c.id as c_id,
                    c.license_plate as c_license_plate,
                    c.brand as c_brand,
                    c.model as c_model,
                    c.body_width as c_body_width,
                    c.body_height as c_body_height,
                    c.body_length as c_body_length,
                    c.cargo_capacity as c_cargo_capacity,
                    d.id as d_id,
                    d.firstname as d_firstname,
                    d.lastname as d_lastname,
                    d.surname as d_surname,
                    d.email as d_email,
                    d.phone_number as d_phone_number
                FROM transport_order o
                JOIN consumer cm ON o.consumer_id = cm.id
                JOIN car c ON o.car_id = c.id
                JOIN driver d ON o.driver_id = d.id
                WHERE o.car_id = ?
                ORDER BY o.created_at DESC
                LIMIT ? OFFSET ?
                """;

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transport_order WHERE car_id = ?",
                Integer.class,
                carId
        );

        List<TransportOrder> transportOrders = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    return toTransportOrder(rs);
                },
                carId,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        return new PageImpl<>(transportOrders, pageable, total);
    }

    private TransportOrder toTransportOrder(ResultSet rs) throws SQLException {
        Consumer consumer =  Consumer.builder()
                .id(rs.getLong("cm_id"))
                .firstname(rs.getObject("cm_firstname", String.class))
                .lastname(rs.getObject("cm_lastname", String.class))
                .surname(rs.getObject("cm_surname", String.class))
                .email(rs.getObject("cm_email", String.class))
                .phoneNumber(rs.getObject("cm_phone_number", String.class))
                .build();

        Driver driver = Driver.builder()
                .id(rs.getLong("d_id"))
                .firstname(rs.getObject("d_firstname", String.class))
                .lastname(rs.getObject("d_lastname", String.class))
                .surname(rs.getObject("d_surname", String.class))
                .email(rs.getObject("d_email", String.class))
                .phoneNumber(rs.getObject("d_phone_number", String.class))
                .build();

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

        return TransportOrder.builder()
                .id(rs.getLong("o_id"))
                .consumer(consumer)
                .car(car)
                .driver(driver)
                .cost(rs.getObject("o_cost", BigDecimal.class))
                .departurePlace(rs.getObject("o_departure_place", String.class))
                .arrivalPlace(rs.getObject("o_arrival_place", String.class))
                .departureTime(rs.getObject("o_departure_time", LocalDateTime.class))
                .arrivalTime(rs.getObject("o_arrival_time", LocalDateTime.class))
                .createdAt(rs.getObject("o_created_at", LocalDateTime.class))
                .weight(rs.getObject("o_weight", Integer.class))
                .width(rs.getObject("o_width", Integer.class))
                .height(rs.getObject("o_height", Integer.class))
                .length(rs.getObject("o_length", Integer.class))
                .build();
    }

    public Page<TransportOrder> findByConsumerId(Long consumerId, Pageable pageable) {
        String sql = """
                SELECT
                    o.id as o_id,
                    o.cost as o_cost,
                    o.departure_place as o_departure_place,
                    o.arrival_place as o_arrival_place,
                    o.departure_time as o_departure_time,
                    o.arrival_time as o_arrival_time,
                    o.created_at as o_created_at,
                    o.weight as o_weight,
                    o.width as o_width,
                    o.height as o_height,
                    o.length as o_length,
                    cm.id as cm_id,
                    cm.firstname as cm_firstname,
                    cm.lastname as cm_lastname,
                    cm.surname as cm_surname,
                    cm.email as cm_email,
                    cm.phone_number as cm_phone_number,
                    c.id as c_id,
                    c.license_plate as c_license_plate,
                    c.brand as c_brand,
                    c.model as c_model,
                    c.body_width as c_body_width,
                    c.body_height as c_body_height,
                    c.body_length as c_body_length,
                    c.cargo_capacity as c_cargo_capacity,
                    d.id as d_id,
                    d.firstname as d_firstname,
                    d.lastname as d_lastname,
                    d.surname as d_surname,
                    d.email as d_email,
                    d.phone_number as d_phone_number
                FROM transport_order o
                JOIN consumer cm ON o.consumer_id = cm.id
                JOIN car c ON o.car_id = c.id
                JOIN driver d ON o.driver_id = d.id
                WHERE o.consumer_id = ?
                ORDER BY o.created_at DESC
                LIMIT ? OFFSET ?
                """;

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transport_order WHERE consumer_id = ?",
                Integer.class,
                consumerId
        );

        List<TransportOrder> transportOrders = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    return toTransportOrder(rs);
                },
                consumerId,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        return new PageImpl<>(transportOrders, pageable, total);
    }

    public Optional<TransportOrder> findById(Long id) {
        String sql = """
                SELECT
                    o.id as o_id,
                    o.cost as o_cost,
                    o.departure_place as o_departure_place,
                    o.arrival_place as o_arrival_place,
                    o.departure_time as o_departure_time,
                    o.arrival_time as o_arrival_time,
                    o.created_at as o_created_at,
                    o.weight as o_weight,
                    o.width as o_width,
                    o.height as o_height,
                    o.length as o_length,
                    cm.id as cm_id,
                    cm.firstname as cm_firstname,
                    cm.lastname as cm_lastname,
                    cm.surname as cm_surname,
                    cm.email as cm_email,
                    cm.phone_number as cm_phone_number,
                    c.id as c_id,
                    c.license_plate as c_license_plate,
                    c.brand as c_brand,
                    c.model as c_model,
                    c.body_width as c_body_width,
                    c.body_height as c_body_height,
                    c.body_length as c_body_length,
                    c.cargo_capacity as c_cargo_capacity,
                    d.id as d_id,
                    d.firstname as d_firstname,
                    d.lastname as d_lastname,
                    d.surname as d_surname,
                    d.email as d_email,
                    d.phone_number as d_phone_number
                FROM transport_order o
                JOIN consumer cm ON o.consumer_id = cm.id
                JOIN car c ON o.car_id = c.id
                JOIN driver d ON o.driver_id = d.id
                WHERE o.id = ?
                """;

        try {
            TransportOrder transportOrder = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> toTransportOrder(rs),
                    id
            );
            return Optional.ofNullable(transportOrder);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Page<TransportOrder> findAll(Pageable pageable){
        String sql = """
                SELECT
                    o.id as o_id,
                    o.cost as o_cost,
                    o.departure_place as o_departure_place,
                    o.arrival_place as o_arrival_place,
                    o.departure_time as o_departure_time,
                    o.arrival_time as o_arrival_time,
                    o.created_at as o_created_at,
                    o.weight as o_weight,
                    o.width as o_width,
                    o.height as o_height,
                    o.length as o_length,
                    cm.id as cm_id,
                    cm.firstname as cm_firstname,
                    cm.lastname as cm_lastname,
                    cm.surname as cm_surname,
                    cm.email as cm_email,
                    cm.phone_number as cm_phone_number,
                    c.id as c_id,
                    c.license_plate as c_license_plate,
                    c.brand as c_brand,
                    c.model as c_model,
                    c.body_width as c_body_width,
                    c.body_height as c_body_height,
                    c.body_length as c_body_length,
                    c.cargo_capacity as c_cargo_capacity,
                    d.id as d_id,
                    d.firstname as d_firstname,
                    d.lastname as d_lastname,
                    d.surname as d_surname,
                    d.email as d_email,
                    d.phone_number as d_phone_number
                FROM transport_order o
                JOIN consumer cm ON o.consumer_id = cm.id
                JOIN car c ON o.car_id = c.id
                JOIN driver d ON o.driver_id = d.id
                ORDER BY o.created_at DESC
                LIMIT ? OFFSET ?
                """;

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transport_order",
                Integer.class
        );

        List<TransportOrder> transportOrders = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    return toTransportOrder(rs);
                },
                pageable.getPageSize(),
                pageable.getOffset()
                );

        return new PageImpl<>(transportOrders, pageable, total);
    }

    public List<TransportOrder> findAllForExport(){
        String sql = """
                SELECT
                    o.id as o_id,
                    o.cost as o_cost,
                    o.departure_place as o_departure_place,
                    o.arrival_place as o_arrival_place,
                    o.departure_time as o_departure_time,
                    o.arrival_time as o_arrival_time,
                    o.created_at as o_created_at,
                    o.weight as o_weight,
                    o.width as o_width,
                    o.height as o_height,
                    o.length as o_length,
                    cm.id as cm_id,
                    cm.firstname as cm_firstname,
                    cm.lastname as cm_lastname,
                    cm.surname as cm_surname,
                    cm.email as cm_email,
                    cm.phone_number as cm_phone_number,
                    c.id as c_id,
                    c.license_plate as c_license_plate,
                    c.brand as c_brand,
                    c.model as c_model,
                    c.body_width as c_body_width,
                    c.body_height as c_body_height,
                    c.body_length as c_body_length,
                    c.cargo_capacity as c_cargo_capacity,
                    d.id as d_id,
                    d.firstname as d_firstname,
                    d.lastname as d_lastname,
                    d.surname as d_surname,
                    d.email as d_email,
                    d.phone_number as d_phone_number
                FROM transport_order o
                JOIN consumer cm ON o.consumer_id = cm.id
                JOIN car c ON o.car_id = c.id
                JOIN driver d ON o.driver_id = d.id
                ORDER BY o.created_at DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> toTransportOrder(rs)
        );
    }

    public Page<TransportOrder> findByDriverId(Long driverId, Pageable pageable) {
        String sql = """
                SELECT
                    o.id as o_id,
                    o.cost as o_cost,
                    o.departure_place as o_departure_place,
                    o.arrival_place as o_arrival_place,
                    o.departure_time as o_departure_time,
                    o.arrival_time as o_arrival_time,
                    o.created_at as o_created_at,
                    o.weight as o_weight,
                    o.width as o_width,
                    o.height as o_height,
                    o.length as o_length,
                    cm.id as cm_id,
                    cm.firstname as cm_firstname,
                    cm.lastname as cm_lastname,
                    cm.surname as cm_surname,
                    cm.email as cm_email,
                    cm.phone_number as cm_phone_number,
                    c.id as c_id,
                    c.license_plate as c_license_plate,
                    c.brand as c_brand,
                    c.model as c_model,
                    c.body_width as c_body_width,
                    c.body_height as c_body_height,
                    c.body_length as c_body_length,
                    c.cargo_capacity as c_cargo_capacity,
                    d.id as d_id,
                    d.firstname as d_firstname,
                    d.lastname as d_lastname,
                    d.surname as d_surname,
                    d.email as d_email,
                    d.phone_number as d_phone_number
                FROM transport_order o
                JOIN consumer cm ON o.consumer_id = cm.id
                JOIN car c ON o.car_id = c.id
                JOIN driver d ON o.driver_id = d.id
                WHERE o.driver_id = ?
                ORDER BY o.created_at DESC
                LIMIT ? OFFSET ?
                """;

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transport_order WHERE driver_id = ?",
                Integer.class,
                driverId
        );

        List<TransportOrder> transportOrders = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    return toTransportOrder(rs);
                },
                driverId,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        return new PageImpl<>(transportOrders, pageable, total);
    }
}
