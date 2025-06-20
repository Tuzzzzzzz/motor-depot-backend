package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.Mechanic;
import by.evgen.motor_depot.database.repository.MechanicRepository;
import by.evgen.motor_depot.dto.request.MechanicRequest;
import by.evgen.motor_depot.dto.response.MechanicResponse;
import by.evgen.motor_depot.mapper.MechanicMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MechanicService {
    private final MechanicRepository mechanicRepository;
    private final MechanicMapper mechanicMapper;
    private final TemplateEngine templateEngine;

    @Transactional
    public MechanicResponse create(MechanicRequest request){
        Mechanic mechanic = Mechanic.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .surname(request.surname())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();

        return mechanicMapper.toResponse(mechanicRepository.create(mechanic));
    }

    @Transactional
    public MechanicResponse update(Long id, MechanicRequest request){
        Mechanic mechanic = mechanicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Механик с id=%d не найден".formatted(id)
                ));

        mechanic.setFirstname(request.firstname());
        mechanic.setLastname(request.lastname());
        mechanic.setSurname(request.surname());
        mechanic.setEmail(request.email());
        mechanic.setPhoneNumber(request.phoneNumber());
        mechanicRepository.update(mechanic);
        return mechanicMapper.toResponse(mechanic);
    }

    @Transactional
    public void delete(Long id){
        mechanicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Механик с id=%d не найден".formatted(id)
                ));
        mechanicRepository.delete(id);
    }

    public Page<MechanicResponse> findAll(Pageable pageable){
        return mechanicRepository.findAll(pageable)
                .map(mechanicMapper::toResponse);
    }

    public MechanicResponse findById(Long id) {
        return mechanicRepository.findById(id)
                .map(mechanicMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Механик с id=%d не найден".formatted(id)
                ));
    }
}
