package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.Driver;
import by.evgen.motor_depot.database.repository.DriverRepository;
import by.evgen.motor_depot.dto.request.DriverRequest;
import by.evgen.motor_depot.dto.response.DriverResponse;
import by.evgen.motor_depot.mapper.DriverMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Transactional
    public DriverResponse create(DriverRequest request){
        Driver driver = Driver.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .surname(request.surname())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();

        return driverMapper.toResponse(driverRepository.create(driver));
    }

    @Transactional
    public DriverResponse update(Long id, DriverRequest request){
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Водитель с id=%d не найден".formatted(id)
                ));

        driver.setFirstname(request.firstname());
        driver.setLastname(request.lastname());
        driver.setSurname(request.surname());
        driver.setEmail(request.email());
        driver.setPhoneNumber(request.phoneNumber());
        driverRepository.update(driver);
        return driverMapper.toResponse(driver);
    }

    @Transactional
    public void delete(Long id){
        driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Водитель с id=%d не найден".formatted(id)
                ));
        driverRepository.delete(id);
    }

    public Page<DriverResponse> findAll(Pageable pageable){
        return driverRepository.findAll(pageable)
                .map(driverMapper::toResponse);
    }

    public DriverResponse findById(Long id) {
        return driverRepository.findById(id)
                .map(driverMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Водитель с id=%d не найден".formatted(id)
                ));
    }
}

