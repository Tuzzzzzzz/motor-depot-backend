package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.dto.request.ConsumerRequest;
import by.evgen.motor_depot.dto.response.ConsumerResponse;
import by.evgen.motor_depot.dto.response.TransportOrderResponse;
import by.evgen.motor_depot.service.ConsumerService;
import by.evgen.motor_depot.service.TransportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consumers")
@RequiredArgsConstructor
public class ConsumerController {
    private final ConsumerService consumerService;
    private final TransportOrderService transportOrderService;

    @PostMapping
    public ConsumerResponse create(@RequestBody ConsumerRequest request){
        return consumerService.create(request);
    }

    @PutMapping("/{id}")
    public ConsumerResponse update(
            @PathVariable("id") Long id,
            @RequestBody ConsumerRequest request
    ){
        return consumerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        consumerService.delete(id);
    }

    @GetMapping
    Page<ConsumerResponse> findAll(Pageable pageable){
        return consumerService.findAll(pageable);
    }

    @GetMapping("/{id}/orders")
    public Page<TransportOrderResponse> findByConsumerId(
            @PathVariable("id") Long consumerId,
            Pageable pageable
    ){
        return transportOrderService.findByConsumerId(consumerId, pageable);
    }

    @GetMapping("/{id}")
    public ConsumerResponse findById(@PathVariable Long id){
        return consumerService.findById(id);
    }
}
