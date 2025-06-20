package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.dto.request.DriverRequest;
import by.evgen.motor_depot.dto.response.CarResponse;
import by.evgen.motor_depot.dto.response.DriverResponse;
import by.evgen.motor_depot.dto.response.TransportOrderResponse;
import by.evgen.motor_depot.service.CarDriverService;
import by.evgen.motor_depot.service.CarService;
import by.evgen.motor_depot.service.DriverService;
import by.evgen.motor_depot.service.TransportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;
    private final CarService carService;
    private final CarDriverService carDriverService;
    private final TransportOrderService transportOrderService;

    @PostMapping
    public DriverResponse create(@RequestBody DriverRequest request){
        return driverService.create(request);
    }

    @PutMapping("/{id}")
    public DriverResponse update(
            @PathVariable("id") Long id,
            @RequestBody DriverRequest request
    ){
        return driverService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        driverService.delete(id);
    }

    @GetMapping
    public Page<DriverResponse> findAll(Pageable pageable){
        return driverService.findAll(pageable);
    }

    @GetMapping("/{id}/cars")
    public List<CarResponse> findAttachedCars(
            @PathVariable("id") Long id
    ){
        return carService.findByDriverId(id);
    }

    @DeleteMapping("/{driverId}/cars/{carId}")
    public void unattachCar(
            @PathVariable Long driverId,
            @PathVariable Long carId
    ){
        carDriverService.unattachCar(driverId, carId);
    }

    @PostMapping("/{driverId}/cars/{carId}")
    public void attachCar(
            @PathVariable Long driverId,
            @PathVariable Long carId
    ){
        carDriverService.attachCar(driverId, carId);
    }

    @GetMapping("/{id}")
    public DriverResponse findById(@PathVariable Long id){
        return driverService.findById(id);
    }

    @GetMapping("/{id}/orders")
    public Page<TransportOrderResponse> findOrders(@PathVariable Long id, Pageable pageable){
        return transportOrderService.findByDriverId(id, pageable);
    }
}
