package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.dto.request.technical_service.TechnicalServiceCreateRequest;
import by.evgen.motor_depot.dto.request.technical_service.TechnicalServiceUpdateRequest;
import by.evgen.motor_depot.dto.response.TechnicalServiceResponse;
import by.evgen.motor_depot.service.TechnicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/technical-services")
@RequiredArgsConstructor
public class TechnicalServiceController {
    private final TechnicalServiceService technicalServiceService;

    @PostMapping
    public TechnicalServiceResponse create(@RequestBody TechnicalServiceCreateRequest request){
        return technicalServiceService.create(request);
    }

    @GetMapping
    public Page<TechnicalServiceResponse> findAll(Pageable pageable){
        return technicalServiceService.findAll(pageable);
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable("id") Long id,
            TechnicalServiceUpdateRequest request
    ) {
        technicalServiceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        technicalServiceService.delete(id);
    }

    @GetMapping("/{id}")
    public TechnicalServiceResponse findById(@PathVariable Long id){
        return technicalServiceService.findById(id);
    }
}
