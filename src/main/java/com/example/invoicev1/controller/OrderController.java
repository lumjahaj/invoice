package com.example.invoicev1.controller;


import com.example.invoicev1.dto.CreateOrdertDTO;
import com.example.invoicev1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(path = "/addOrder")
    public ResponseEntity<String> addOrder(@RequestBody CreateOrdertDTO createOrdertDTO) {
        orderService.saveOrder(createOrdertDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
