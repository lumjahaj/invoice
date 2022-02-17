package com.example.invoicev1.controller;


import com.example.invoicev1.dto.CreateOrdertDTO;
import com.example.invoicev1.dto.ViewOrderDTO;
import com.example.invoicev1.entity.Order;
import com.example.invoicev1.repository.OrderRepository;
import com.example.invoicev1.service.InvoiceService;
import com.example.invoicev1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping(path = "/api")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(path = "/addOrder")
    public Order addOrder(@RequestBody CreateOrdertDTO createOrdertDTO) {
        Optional<Order> optionalOrder = orderService.saveOrder(createOrdertDTO);
        if (optionalOrder.isEmpty()) {
            throw new IllegalStateException("order with this id does not exist!");
        }
        return optionalOrder.get();
    }

    @GetMapping(path = "/getOrder/{id}")
    public ViewOrderDTO getOrderById(@PathVariable Long id){
        return orderService.getOrderById(id);
    }
}
