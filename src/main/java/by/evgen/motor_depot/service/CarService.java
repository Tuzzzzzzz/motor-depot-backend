package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.Car;
import by.evgen.motor_depot.database.repository.CarRepository;
import by.evgen.motor_depot.dto.request.car.CarCreateRequest;
import by.evgen.motor_depot.dto.request.car.CarUpdateRequest;
import by.evgen.motor_depot.dto.response.CarResponse;
import by.evgen.motor_depot.mapper.CarMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Transactional
    public CarResponse create(CarCreateRequest request) {
        Car car = Car.builder()
                .licensePlate(request.licensePlate())
                .brand(request.brand())
                .model(request.model())
                .bodyWidth(request.bodyWidth())
                .bodyHeight(request.bodyHeight())
                .bodyLength(request.bodyLength())
                .cargoCapacity(request.cargoCapacity())
                .build();

        return carMapper.toResponse(carRepository.create(car));
    }

    @Transactional
    public CarResponse update(Long id, CarUpdateRequest request) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Автомобиль с id=%d не найден".formatted(id)
                ));

        car.setLicensePlate(request.licensePlate());
        carRepository.update(car);
        return carMapper.toResponse(car);
    }

    @Transactional
    public void delete(Long id) {
        carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Автомобиль с id=%d не найден".formatted(id)
                ));
        carRepository.delete(id);
    }

    public Page<CarResponse> findAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toResponse);
    }

    public List<CarResponse> findByDriverId(Long driverId) {
        return carRepository.findByDriverId(driverId).stream()
                .map(carMapper::toResponse).toList();
    }

    public CarResponse findById(Long id) {
        return carRepository.findById(id)
                .map(carMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Автомобиль с id=%d не найден".formatted(id)
                ));
    }
}
