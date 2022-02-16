package com.example.invoicev1;

import com.example.invoicev1.entity.Invoice;
import com.example.invoicev1.entity.InvoiceProduct;
import com.example.invoicev1.entity.Order;
import com.example.invoicev1.repository.OrderRepository;
import com.example.invoicev1.service.InvoiceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@SpringBootApplication
public class InvoiceV1Application {
    public static void main(String[] args) {
        SpringApplication.run(InvoiceV1Application.class, args);
    }

    @Bean
    CommandLineRunner run(OrderRepository orderRepository, InvoiceService invoiceService) {
        return args -> {
//            Optional<Order> order = orderRepository.findById(1L);
//            invoiceService.saveInvoices(order.get());
        };
    }

}
