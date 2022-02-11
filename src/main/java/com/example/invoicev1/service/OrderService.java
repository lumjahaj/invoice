package com.example.invoicev1.service;

import com.example.invoicev1.dto.CreateOrdertDTO;
import com.example.invoicev1.dto.CreateProductDTO;
import com.example.invoicev1.entity.Order;
import com.example.invoicev1.entity.Product;
import com.example.invoicev1.repository.OrderRepository;
import com.example.invoicev1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InvoiceService invoiceService;


    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, InvoiceService invoiceService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.invoiceService = invoiceService;
    }

    public void saveOrder(CreateOrdertDTO createOrderDTO) {
        Order order = new Order();
        List<CreateProductDTO> productsDTO = createOrderDTO.getProducts();
        List<Product> products = new ArrayList<>();
        BigDecimal subtotal, VATtotal, total;
        BigDecimal orderSubtotal = BigDecimal.valueOf(0);
        BigDecimal orderVATtotal = BigDecimal.valueOf(0);
        for (CreateProductDTO productDTO : productsDTO) {
            //set product attributes
            Product product = new Product(order);
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setVAT(productDTO.getVAT());
            product.setDiscount(productDTO.getDiscount());
            product.setQtyAvailable(productDTO.getQtyAvailable());

            // calculate and set totals for product, add product to arraylist
            BigDecimal[] productTotals = product.calculateProductTotal();
            BigDecimal qty = BigDecimal.valueOf(productDTO.getQtyAvailable());
            subtotal = productTotals[0].multiply(qty);
            VATtotal = productTotals[1].multiply(qty);
            total = subtotal.add(VATtotal);
            product.setSubtotal(subtotal);
            product.setVATtotal(VATtotal);
            product.setTotal(total);
            products.add(product);

            //calculate order totals
            orderSubtotal = orderSubtotal.add(subtotal);
            orderVATtotal = orderVATtotal.add(VATtotal);

        }
        order.setSubtotal(orderSubtotal);
        order.setVATtotal(orderVATtotal);
        order.setTotal(orderSubtotal.add(orderVATtotal));
        orderRepository.save(order);
        productRepository.saveAll(products);
//        invoiceService.saveInvoices(order);
    }

}
