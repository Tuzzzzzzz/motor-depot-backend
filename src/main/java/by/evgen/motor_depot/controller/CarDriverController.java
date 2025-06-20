package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.dto.request.car_driver.OrderParamsRequest;
import by.evgen.motor_depot.dto.response.CarDriverResponse;
import by.evgen.motor_depot.service.CarDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/car-driver-pairs")
@RequiredArgsConstructor
public class CarDriverController {
    private final CarDriverService carDriverService;

    @PostMapping("/search")
    public Page<CarDriverResponse> findByCargoParameters(
            @RequestBody OrderParamsRequest request,
            Pageable pageable
    ){
        return carDriverService.findByCargoParameters(request, pageable);
    }

    @GetMapping
    public Page<CarDriverResponse> findAll(Pageable pageable){
        return carDriverService.findAll(pageable);
    }
}
