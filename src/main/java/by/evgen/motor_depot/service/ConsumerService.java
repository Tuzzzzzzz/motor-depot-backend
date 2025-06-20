package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.Consumer;
import by.evgen.motor_depot.database.repository.ConsumerRepository;
import by.evgen.motor_depot.dto.request.ConsumerRequest;
import by.evgen.motor_depot.dto.response.ConsumerResponse;
import by.evgen.motor_depot.mapper.ConsumerMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumerService {
    private final ConsumerRepository consumerRepository;
    private final ConsumerMapper consumerMapper;

    @Transactional
    public ConsumerResponse create(ConsumerRequest request){
        Consumer consumer = Consumer.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .surname(request.surname())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();

        return consumerMapper.toResponse(consumerRepository.create(consumer));
    }

    @Transactional
    public ConsumerResponse update(Long id, ConsumerRequest request){
        Consumer consumer = consumerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Заказчик с id=%d не найден".formatted(id)
                ));

        consumer.setFirstname(request.firstname());
        consumer.setLastname(request.lastname());
        consumer.setSurname(request.surname());
        consumer.setEmail(request.email());
        consumer.setPhoneNumber(request.phoneNumber());
        consumerRepository.update(consumer);
        return consumerMapper.toResponse(consumer);
    }

    @Transactional
    public void delete(Long id){
        consumerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Заказчик с id=%d не найден".formatted(id)
                ));
        consumerRepository.delete(id);
    }

    public Page<ConsumerResponse> findAll(Pageable pageable){
        return consumerRepository.findAll(pageable)
                .map(consumerMapper::toResponse);
    }

    public ConsumerResponse findById(Long id) {
        return consumerRepository.findById(id)
                .map(consumerMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Заказчик с id=%d не найден".formatted(id)
                ));
    }
}
