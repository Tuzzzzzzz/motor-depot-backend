package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.dto.request.car.CarCreateRequest;
import by.evgen.motor_depot.dto.request.car.CarUpdateRequest;
import by.evgen.motor_depot.dto.response.CarResponse;
import by.evgen.motor_depot.dto.response.TechnicalServiceResponse;
import by.evgen.motor_depot.dto.response.TransportOrderResponse;
import by.evgen.motor_depot.service.CarService;
import by.evgen.motor_depot.service.TechnicalServiceService;
import by.evgen.motor_depot.service.TransportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    private final TransportOrderService transportOrderService;
    private final TechnicalServiceService technicalServiceService;

    @PostMapping
    public CarResponse create(@RequestBody CarCreateRequest request){
        return carService.create(request);
    }

    @GetMapping
    public Page<CarResponse> findAll(Pageable pageable){
        return carService.findAll(pageable);
    }

    @PutMapping("/{id}")
    public CarResponse update(
            @PathVariable("id") Long id,
            @RequestBody CarUpdateRequest request
    ){
        return carService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        carService.delete(id);
    }

    @GetMapping("/{id}/orders")
    public Page<TransportOrderResponse> findOrders(
            @PathVariable("id") Long id,
            Pageable pageable
    ){
        return transportOrderService.findByCarId(id, pageable);
    }

    @GetMapping("/{id}/technical-services")
    public Page<TechnicalServiceResponse> findTechnicalServices(
            @PathVariable("id") Long id,
            Pageable pageable
    ){
        return technicalServiceService.findByCarId(id, pageable);
    }

    @GetMapping("/{id}")
    public CarResponse findById(@PathVariable Long id){
        return carService.findById(id);
    }
}
