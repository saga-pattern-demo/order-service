package com.saga.orderservice.controller;

import com.saga.common.dto.OrderRequestDTO;
import com.saga.common.dto.OrderResponseDTO;
import com.saga.orderservice.entity.Order;
import com.saga.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService service;

    @PostMapping("")
    public Order createOrder(@RequestBody OrderRequestDTO dto){
        return service.createOrder(dto);
    }

    @GetMapping("")
    public List<OrderResponseDTO> getOrders(){
        return service.getAll();
    }
}
