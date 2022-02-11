package com.example.invoicev1;

import com.example.invoicev1.entity.Invoice;
import com.example.invoicev1.entity.Order;
import com.example.invoicev1.entity.Product;
import com.example.invoicev1.repository.InvoiceRepository;
import com.example.invoicev1.repository.OrderRepository;
import com.example.invoicev1.repository.ProductRepository;
import com.example.invoicev1.service.InvoiceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SpringBootApplication
public class InvoiceV1Application {
    public static void main(String[] args) {
        SpringApplication.run(InvoiceV1Application.class, args);
    }

    @Bean
    CommandLineRunner run(OrderRepository orderRepository, InvoiceRepository invoiceRepository, InvoiceService invoiceService) {
        return args -> {

//            Product product1 = new Product(null, "Coca Cola", 0.50, 18, 0.1, 77);
//            Product product2 = new Product(null, "Water", 0.25, 8, 0, 240);
//            Product product3 = new Product(null, "Chocolate", 1.25, 22, 0, 38);
//            productRepository.save(product1);
//            productRepository.save(product2);
//            productRepository.save(product3);
//            double total = product1.calculateProductTotal();
//            System.out.println();
//            System.out.println(total);
//
//            Invoice invoice = new Invoice(null, 0, 0 ,0);
//            invoice.setInvoiceProducts(new ArrayList<>(List.of(product1, product2)));
//            invoiceRepository.save(invoice);
//            Invoice invoice2 = new Invoice(null, 0, 0 ,0);
//            invoice2.setInvoiceProducts(new ArrayList<>(List.of(product3, product2)));
//            invoiceRepository.save(invoice2);
//            System.out.println();
//            List<Product> products = productRepository.findByInvoices(invoice);
//            for (Product p :
//                    products) {
//                System.out.println(p);
//            }
//            List<Product> products2 = productRepository.findByInvoices(invoice2);
//            for (Product p :
//                    products2) {
//                System.out.println(p);
//            }

//            Invoice invoice = new Invoice(null, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0));
//            List<Product> products = productRepository.findAll();
//            System.out.println(invoice.splitInvoices(products));
//            System.out.println(Arrays.toString(invoice.splitInvoices(products)));
//            for (Product p : invoice.splitInvoices(products)) {
//                System.out.println(p.getDescription());
//            }


//            Optional<Order> order = orderRepository.findById(1L);
//            invoiceService.saveInvoices(order.get());
        };
    }

}
