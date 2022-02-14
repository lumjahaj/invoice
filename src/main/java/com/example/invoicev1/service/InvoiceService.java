package com.example.invoicev1.service;

import com.example.invoicev1.entity.Invoice;
import com.example.invoicev1.entity.InvoiceProduct;
import com.example.invoicev1.entity.Order;
import com.example.invoicev1.entity.Product;
import com.example.invoicev1.repository.InvoiceProductRepository;
import com.example.invoicev1.repository.InvoiceRepository;
import com.example.invoicev1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final InvoiceProductRepository invoiceProductRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, ProductRepository productRepository, InvoiceProductRepository invoiceProductRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
        this.invoiceProductRepository = invoiceProductRepository;
    }

    public void saveInvoices(Order order) {
        List<Product> orderedProducts = order.getOrderProducts();
        List<Invoice> invoices = new ArrayList<>();
        generateInvoices(orderedProducts, invoices);
    }

    public void generateInvoices(List<Product> orderedProducts, List<Invoice> invoices) {
        Map<Product, Integer> tempProducts = new HashMap<>();
        List<Product> deletedProducts = new ArrayList<>();
        BigDecimal tempAmount = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (Product product : orderedProducts) {
            total = calculatedPrice(product,product.getQtyAvailable()).get("total");
            if (product.getPrice().compareTo(BigDecimal.valueOf(500)) > 0 && tempProducts.isEmpty()) {
                Invoice invoice = new Invoice();
                invoiceRepository.save(invoice);
                InvoiceProduct invoiceProduct = new InvoiceProduct(null, invoice, product, 1);
                invoice.setInvoiceProducts(Set.of(invoiceProduct));
                product.setInvoiceProducts(Set.of(invoiceProduct));
                invoice.setTotal(product.calculateProductTotal()[2]);
                productRepository.save(product);
                invoiceRepository.save(invoice);
                invoiceProductRepository.save(invoiceProduct);
//                tempProducts.put(product, 1);
//                tempAmount = tempAmount.add(calculatedPrice(product, 1).get("total"));
                if (product.getQtyAvailable() > 1) {
                    product.setQtyAvailable(product.getQtyAvailable() - 1);
                    productRepository.save(product);
                } else {
                    product.setQtyAvailable(0);
                    productRepository.save(product);
                    deletedProducts.add(product);
                }
            } else if (tempAmount.compareTo(BigDecimal.valueOf(500)) < 0) {
                if (product.getQtyAvailable() <= 50 && product.checkProductQtyTotalOverValue(500, product.getQtyAvailable())) {
                    tempProducts.put(product, product.getQtyAvailable());
                    tempAmount = tempAmount.add(calculatedPrice(product, product.getQtyAvailable()).get("total"));
                    product.setQtyAvailable(0);
                    deletedProducts.add(product);
                    productRepository.save(product);
                }else if (product.checkProductQtyTotalOverValue(500,1)){
//                if (product.getQtyAvailable() <= 50 && product.getQtyAvailable() > 0 && product.checkProductQtyTotalOverValue(500, product.getQtyAvailable())) {
                    tempProducts.put(product, 50);
                    tempAmount = tempAmount.add(calculatedPrice(product, 50).get("total"));
                    product.setQtyAvailable(product.getQtyAvailable() - 50);
                    productRepository.save(product);
                }
            }
        }
        orderedProducts.removeAll(deletedProducts);
        Invoice invoice = new Invoice();
        invoice.setTotal(tempAmount);
        invoiceRepository.save(invoice);
        for (Map.Entry<Product, Integer> entry : tempProducts.entrySet()) {
            InvoiceProduct invoiceProduct = new InvoiceProduct(null, invoice, entry.getKey(), entry.getValue());
            invoice.getInvoiceProducts().add(invoiceProduct);
            invoiceRepository.save(invoice);
            entry.getKey().getInvoiceProducts().add(invoiceProduct);
            productRepository.save(entry.getKey());
            invoiceProductRepository.save(invoiceProduct);
        }
        invoices.add(invoice);
        tempProducts.clear();
        if (!orderedProducts.isEmpty()) {
            generateInvoices(orderedProducts, invoices);
        }
    }

    public Map<String, BigDecimal> calculatedPrice(Product product, int qty) {
        BigDecimal subtotal, total, VATtotal;
        if (!(product.getDiscount().equals(BigDecimal.valueOf(0)))) {
            subtotal = product.getPrice().subtract(product.getDiscount());
        } else {
            subtotal = product.getPrice();
        }
        VATtotal = subtotal.multiply((BigDecimal.valueOf(product.getVAT())).multiply(BigDecimal.valueOf(0.01)));
        total = subtotal.add(VATtotal);
        BigDecimal subtotalQty = subtotal.multiply(BigDecimal.valueOf(qty));
        BigDecimal VATtotalQty = VATtotal.multiply(BigDecimal.valueOf(qty));
        BigDecimal totalQty = total.multiply(BigDecimal.valueOf(qty));

        return Map.of("subtotal", subtotalQty, "VATtotal", VATtotalQty, "total", totalQty);
    }

    public Invoice updateInvoice(Invoice invoice) {
        Invoice existingInvoice = invoiceRepository.findById(invoice.getId()).orElse(null);
        existingInvoice.setSubtotal(invoice.getSubtotal());
        return invoiceRepository.save(existingInvoice);
    }

}

