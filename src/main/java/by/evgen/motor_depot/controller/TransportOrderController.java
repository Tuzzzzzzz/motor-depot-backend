package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.dto.request.transport_order.TransportOrderCreateRequest;
import by.evgen.motor_depot.dto.request.transport_order.TransportOrderUpdateRequest;
import by.evgen.motor_depot.dto.response.TransportOrderResponse;
import by.evgen.motor_depot.service.TransportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class TransportOrderController {
    private final TransportOrderService transportOrderService;

    @PostMapping
    public TransportOrderResponse create(@RequestBody TransportOrderCreateRequest request){
        return transportOrderService.create(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        transportOrderService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable("id") Long id,
            @RequestBody TransportOrderUpdateRequest request
    ){
        transportOrderService.update(id, request);
    }

    @GetMapping
    public Page<TransportOrderResponse> findAll(Pageable pageable){
        return transportOrderService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public TransportOrderResponse findById(@PathVariable Long id){
        return transportOrderService.findById(id);
    }
}
