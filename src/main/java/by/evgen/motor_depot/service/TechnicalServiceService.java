package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.TechnicalService;
import by.evgen.motor_depot.dto.request.technical_service.TechnicalServiceUpdateRequest;
import by.evgen.motor_depot.dto.response.TechnicalServiceResponse;
import by.evgen.motor_depot.mapper.TechnicalServiceMapper;
import by.evgen.motor_depot.database.entity.Car;
import by.evgen.motor_depot.database.entity.Mechanic;
import by.evgen.motor_depot.database.repository.CarRepository;
import by.evgen.motor_depot.database.repository.MechanicRepository;
import by.evgen.motor_depot.database.repository.TechnicalServiceRepository;
import by.evgen.motor_depot.dto.request.technical_service.TechnicalServiceCreateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechnicalServiceService {
    private final TechnicalServiceRepository technicalServiceRepository;
    private final CarRepository carRepository;
    private final MechanicRepository mechanicRepository;
    private final TechnicalServiceMapper tsMapper;

    @Transactional
    public TechnicalServiceResponse create(TechnicalServiceCreateRequest request){
        Car car = carRepository.findById(request.carId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Автомобиль с id=%d не найден".formatted(request.carId())
                ));

        Mechanic mechanic = mechanicRepository.findById(request.mechanicId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Механик с id=%d не найден".formatted(request.mechanicId())
                ));

        TechnicalService ts = TechnicalService.builder()
                .car(car)
                .mechanic(mechanic)
                .description(request.description())
                .serviceStartTime(request.serviceStartTime())
                .serviceEndTime(request.serviceEndTime())
                .build();

        return tsMapper.toResponse(technicalServiceRepository.create(ts));
    }

    @Transactional
    public void update(Long id, TechnicalServiceUpdateRequest request){
        TechnicalService ts = technicalServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Технический сервис с id=%d не найден".formatted(id)
                ));

        ts.setServiceStartTime(request.serviceStartTime());
        ts.setServiceEndTime(request.serviceEndTime());
        ts.setDescription(request.description());
    }

    public Page<TechnicalServiceResponse> findAll(Pageable pageable){
        return technicalServiceRepository.findAll(pageable)
                .map(tsMapper::toResponse);
    }

    @Transactional
    public void delete(Long id){
        technicalServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Технический сервис с id=%d не найден".formatted(id)
                ));
        technicalServiceRepository.delete(id);
    }

    public Page<TechnicalServiceResponse> findByCarId(Long carId, Pageable pageable){
        return technicalServiceRepository.findByCarId(pageable, carId)
                .map(tsMapper::toResponse);
    }

    public Page<TechnicalServiceResponse> findByMechanicId(Long id, Pageable pageable) {
        return technicalServiceRepository.findByMechanicId(id, pageable)
                .map(tsMapper::toResponse);
    }

    public TechnicalServiceResponse findById(Long id){
        return technicalServiceRepository.findById(id)
                .map(tsMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Технический сервис с id=%d не найден".formatted(id)
                ));
    }
}
