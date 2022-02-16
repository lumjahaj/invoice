package com.example.invoicev1.controller;


import com.example.invoicev1.dto.CreateOrdertDTO;
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
    private final InvoiceService invoiceService;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderService orderService, InvoiceService invoiceService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
        this.orderRepository = orderRepository;
    }

    @PostMapping(path = "/addOrder")
    public Order addOrder(@RequestBody CreateOrdertDTO createOrdertDTO) {
        Optional<Order> order = orderService.saveOrder(createOrdertDTO);
        return order.get();
    }

    @GetMapping(path = "/getOrder/{id}")
    public Order getOrderById(@PathVariable Long id){
        Optional<Order> order = orderRepository.findById(id);
        return order.get();
    }
}
