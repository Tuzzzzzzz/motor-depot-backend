package by.evgen.motor_depot.database.repository;

import by.evgen.motor_depot.database.entity.Car;
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
public class CarRepository {
    private final JdbcTemplate jdbcTemplate;

    public Car create(Car car){
        Long id = jdbcTemplate.queryForObject(
                """
                        INSERT INTO car (
                            license_plate,
                            brand,
                            model,
                            body_width,
                            body_height,
                            body_length,
                            cargo_capacity
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        RETURNING id""",
                Long.class,
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getBodyWidth(),
                car.getBodyHeight(),
                car.getBodyLength(),
                car.getCargoCapacity()
        );

        car.setId(id);

        return car;
    }

    public Optional<Car> findById(Long id){
        try {
            Car car = jdbcTemplate.queryForObject(
                    "SELECT * FROM car WHERE id = ?",
                    (rs, rowNum) -> toCar(rs),
                    id
            );
            return Optional.ofNullable(car);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Page<Car> findAll(Pageable pageable){
        String sql = "SELECT * FROM car ORDER BY id LIMIT ? OFFSET ?";

        List<Car> cars = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> toCar(rs),
                pageable.getPageSize(),
                pageable.getOffset()
            );

        Integer total = jdbcTemplate.queryForObject(
               "SELECT COUNT(*) FROM car",
               Integer.class
        );

        return new PageImpl<>(cars, pageable, total);
    }

    public void update(Car car) {
        jdbcTemplate.update(
                """
                       UPDATE car
                       SET license_plate = ?,
                           brand = ?,
                           model = ?,
                           body_width = ?,
                           body_height = ?,
                           body_length = ?,
                           cargo_capacity = ?
                       WHERE id = ?""",
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getBodyWidth(),
                car.getBodyHeight(),
                car.getBodyLength(),
                car.getCargoCapacity(),
                car.getId()
        );
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM car WHERE id = ?", id);
    }

    public List<Car> findByDriverId(Long driverId){
        return jdbcTemplate.query(
                """
                        SELECT c.* from car c
                        JOIN car_driver cd ON c.id = cd.car_id
                        WHERE cd.driver_id = ?
                        ORDER BY c.id""",
                (rs, rowNum) -> toCar(rs),
                driverId
        );
    }

    private Car toCar(ResultSet rs) throws SQLException {
        return Car.builder()
                .id(rs.getLong("id"))
                .licensePlate(rs.getObject("license_plate", String.class))
                .brand(rs.getObject("brand", String.class))
                .model(rs.getObject("model", String.class))
                .bodyWidth(rs.getObject("body_width", Integer.class))
                .bodyHeight(rs.getObject("body_height", Integer.class))
                .bodyLength(rs.getObject("body_length", Integer.class))
                .cargoCapacity(rs.getObject("cargo_capacity", Integer.class))
                .build();
    }

    public List<Car> findAllForExport(){
        return jdbcTemplate.query(
                "SELECT * FROM car ORDER BY id",
                (rs, rowNum) -> toCar(rs)
        );
    }
}
