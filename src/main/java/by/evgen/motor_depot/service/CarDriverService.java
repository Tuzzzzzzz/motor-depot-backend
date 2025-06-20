package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.Car;
import by.evgen.motor_depot.database.entity.CarDriver;
import by.evgen.motor_depot.database.entity.Driver;
import by.evgen.motor_depot.database.repository.CarDriverRepository;
import by.evgen.motor_depot.database.repository.CarRepository;
import by.evgen.motor_depot.database.repository.DriverRepository;
import by.evgen.motor_depot.dto.request.car_driver.OrderParamsRequest;
import by.evgen.motor_depot.dto.response.CarDriverResponse;
import by.evgen.motor_depot.mapper.CarDriverMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarDriverService {
    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final CarDriverRepository carDriverRepository;
    private final CarDriverMapper carDriverMapper;

    @Transactional
    public void attachCar(Long driverId, Long carId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Водитель с id=%d не найден".formatted(driverId)
                ));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Автомобиль с id=%d не найден".formatted(carId)
                ));

        CarDriver carDriver = CarDriver.builder()
                .car(car)
                .driver(driver)
                .build();

        carDriverRepository.create(carDriver);
    }

    @Transactional
    public void unattachCar(Long driverId, Long carId) {
        driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Водитель с id=%d не найден".formatted(driverId)
                ));

        carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Автомобиль с id=%d не найден".formatted(carId)
                ));

        if (!carDriverRepository.existsByCarIdAndDriverId(carId, driverId)) {
            throw new EntityNotFoundException(
                    "Водитель с id=%d и автомобиль с id=%d не связаны"
                            .formatted(driverId, carId)
            );
        }

        carDriverRepository.deleteByCarIdAndDriverId(carId, driverId);
    }

    public Page<CarDriverResponse> findByCargoParameters(OrderParamsRequest request, Pageable pageable) {
        return carDriverRepository.findByCargoParameters(
                request.width(),
                request.height(),
                request.length(),
                request.weight(),
                request.departureTime(),
                request.arrivalTime(),
                pageable
        ).map(carDriverMapper::toResponse);
    }

    public Page<CarDriverResponse> findAll(Pageable pageable) {
        return carDriverRepository.findAll(pageable)
                .map(carDriverMapper::toResponse);
    }

}
