package com.example.invoicev1.service;

import com.example.invoicev1.dto.*;
import com.example.invoicev1.entity.Invoice;
import com.example.invoicev1.entity.InvoiceProduct;
import com.example.invoicev1.entity.Order;
import com.example.invoicev1.entity.Product;
import com.example.invoicev1.repository.OrderRepository;
import com.example.invoicev1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Optional<Order> saveOrder(CreateOrdertDTO createOrderDTO) {
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
        return orderRepository.findById(order.getId());
    }

    public ViewOrderDTO getOrderById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new IllegalStateException("order with this id does not exist!");
        }
        Order order = optionalOrder.get();
        ViewOrderDTO orderDTO = new ViewOrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setSubtotal(order.getSubtotal());
        orderDTO.setVATtotal(order.getVATtotal());
        orderDTO.setTotal(order.getTotal());
        List<ViewInvoiceDTO> invoiceDTOS = new ArrayList<>();
        for (Invoice invoice: order.getOrderInvoices()) {
            ViewInvoiceDTO invoiceDTO = new ViewInvoiceDTO();
            invoiceDTO.setId(invoice.getId());
            invoiceDTO.setSubtotal(invoice.getSubtotal());
            invoiceDTO.setVATtotal(invoice.getVATtotal());
            invoiceDTO.setTotal(invoice.getTotal());
            List<ViewProductDTO> productDTOS = new ArrayList<>();
            for (InvoiceProduct invoiceProduct: invoice.getInvoiceProducts()) {
                Product product = invoiceProduct.getProduct();
                ViewProductDTO viewProductDTO = new ViewProductDTO();
                viewProductDTO.setId(product.getId());
                viewProductDTO.setDescription(product.getDescription());
                viewProductDTO.setQty(invoiceProduct.getQty());
                viewProductDTO.setPrice(product.getPrice());
                viewProductDTO.setDiscount(product.getDiscount());
                viewProductDTO.setVAT(product.getVAT());
                viewProductDTO.setVATtotal(invoiceProduct.getVATtotal());
                viewProductDTO.setSubtotal(invoiceProduct.getSubtotal());
                viewProductDTO.setTotal(invoiceProduct.getTotal());
                productDTOS.add(viewProductDTO);
            }
            invoiceDTO.setProducts(productDTOS);
            invoiceDTOS.add(invoiceDTO);
        }
        orderDTO.setInvoices(invoiceDTOS);
        return orderDTO;
    }
}
