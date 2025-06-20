package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.dto.request.MechanicRequest;
import by.evgen.motor_depot.dto.response.MechanicResponse;
import by.evgen.motor_depot.dto.response.TechnicalServiceResponse;
import by.evgen.motor_depot.service.MechanicService;
import by.evgen.motor_depot.service.TechnicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mechanics")
@RequiredArgsConstructor
public class MechanicController {
    private final MechanicService mechanicService;
    private final TechnicalServiceService technicalServiceService;
    
    @PostMapping
    public MechanicResponse create(@RequestBody MechanicRequest request){
        return mechanicService.create(request);
    }

    @PutMapping("/{id}")
    public MechanicResponse update(
            @PathVariable("id") Long id,
            @RequestBody MechanicRequest request
    ){
        return mechanicService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        mechanicService.delete(id);
    }

    @GetMapping
    Page<MechanicResponse> findAll(Pageable pageable){
        return mechanicService.findAll(pageable);
    }

    @GetMapping("/{id}/technical-services")
    public Page<TechnicalServiceResponse> findTechnicalServices(
            @PathVariable("id") Long id,
            Pageable pageable
    ){
        return technicalServiceService.findByMechanicId(id, pageable);
    }

    @GetMapping("/{id}")
    public MechanicResponse findById(@PathVariable Long id){
        return mechanicService.findById(id);
    }
}
