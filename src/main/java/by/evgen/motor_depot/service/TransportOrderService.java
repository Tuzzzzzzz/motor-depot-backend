package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.Car;
import by.evgen.motor_depot.database.entity.Driver;
import by.evgen.motor_depot.database.repository.CarRepository;
import by.evgen.motor_depot.database.repository.DriverRepository;
import by.evgen.motor_depot.dto.request.transport_order.TransportOrderCreateRequest;
import by.evgen.motor_depot.dto.request.transport_order.TransportOrderUpdateRequest;
import by.evgen.motor_depot.database.entity.Consumer;
import by.evgen.motor_depot.database.entity.TransportOrder;
import by.evgen.motor_depot.database.repository.ConsumerRepository;
import by.evgen.motor_depot.database.repository.TransportOrderRepository;
import by.evgen.motor_depot.dto.response.TransportOrderResponse;
import by.evgen.motor_depot.mapper.TransportOrderMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TransportOrderService {
    private final TransportOrderRepository transportOrderRepository;
    private final ConsumerRepository consumerRepository;
    private final TransportOrderMapper transportOrderMapper;
    private final DriverRepository driverRepository;
    private final CarRepository carRepository;

    @Transactional
    public TransportOrderResponse create(TransportOrderCreateRequest request){
        Consumer consumer = consumerRepository.findById(request.consumerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Заказчик с id=%d не найден".formatted(request.consumerId())
                ));

        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Водитель с id=%d не найден".formatted(request.driverId())
                ));

        Car car = carRepository.findById(request.carId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Автомобиль с id=%d не найден".formatted(request.carId())
                ));



        TransportOrder transportOrder = TransportOrder.builder()
                .consumer(consumer)
                .car(car)
                .driver(driver)
                .cost(request.cost())
                .departurePlace(request.departurePlace())
                .arrivalPlace(request.arrivalPlace())
                .departureTime(request.departureTime())
                .arrivalTime(request.arrivalTime())
                .weight(request.weight())
                .width(request.width())
                .height(request.height())
                .length(request.length())
                .build();

        return transportOrderMapper.toResponse(transportOrderRepository.create(transportOrder));
    }

    @Transactional
    public void update(Long id, TransportOrderUpdateRequest request){
        log.info(request.toString());
        TransportOrder transportOrder = transportOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Заказ с id=%d не найден".formatted(id)
                ));

        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Водитель с id=%d не найден".formatted(request.driverId())
                ));

        Car car = carRepository.findById(request.carId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Автомобиль с id=%d не найден".formatted(request.carId())
                ));

        transportOrder.setCar(car);
        transportOrder.setDriver(driver);
        transportOrder.setCost(request.cost());
        transportOrder.setDeparturePlace(request.departurePlace());
        transportOrder.setArrivalPlace(request.arrivalPlace());
        transportOrder.setDepartureTime(request.departureTime());
        transportOrder.setArrivalTime(request.arrivalTime());
        transportOrder.setWeight(request.weight());
        transportOrder.setWidth(request.width());
        transportOrder.setHeight(request.height());
        transportOrder.setLength(request.length());

        transportOrderRepository.update(transportOrder);
    }

    @Transactional
    public void delete(Long id){
        transportOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Заказ с id=%d не найден".formatted(id)
                ));
        transportOrderRepository.delete(id);
    }

    public Page<TransportOrderResponse> findByCarId(Long carId, Pageable pageable){
        return transportOrderRepository.findByCarId(carId, pageable)
                .map(transportOrderMapper::toResponse);
    }

    public Page<TransportOrderResponse> findByConsumerId(Long consumerId, Pageable pageable){
        return transportOrderRepository.findByConsumerId(consumerId, pageable)
                .map(transportOrderMapper::toResponse);
    }

    public Page<TransportOrderResponse> findAll(Pageable pageable){
        return transportOrderRepository.findAll(pageable)
                .map(transportOrderMapper::toResponse);
    }

    public Page<TransportOrderResponse> findByDriverId(Long driverId, Pageable pageable) {
        return transportOrderRepository.findByDriverId(driverId, pageable)
                .map(transportOrderMapper::toResponse);
    }

    public TransportOrderResponse findById(Long id){
        return transportOrderRepository.findById(id)
                .map(transportOrderMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Заказ с id=%d не найден".formatted(id)
                ));
    }
}
