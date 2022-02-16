package com.example.invoicev1.service;

import com.example.invoicev1.dto.InvoiceTotalsDTO;
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

    public List<Invoice> saveInvoices(Order order) {
        List<Product> orderedProducts = order.getOrderProducts();
        List<Invoice> invoices = new ArrayList<>();
        generateInvoices(orderedProducts, invoices, order);
        return invoices;
    }

    public void generateInvoices(List<Product> orderedProducts, List<Invoice> invoices, Order order) {
        Map<Product, Integer> tempProducts = new HashMap<>();
        List<Product> deletedProducts = new ArrayList<>();
        BigDecimal tempTotal = BigDecimal.ZERO;
        BigDecimal tempSubtotal = BigDecimal.ZERO;
        BigDecimal tempVATtotal = BigDecimal.ZERO;
        for (Product product : orderedProducts) {
            if (product.getPrice().compareTo(BigDecimal.valueOf(500)) > 0 && tempProducts.isEmpty()) {
                Invoice invoice = new Invoice();
                invoice.setOrder(order);
                invoiceRepository.save(invoice);
                InvoiceProduct invoiceProduct = new InvoiceProduct(null, invoice, product, 1);
                invoice.setInvoiceProducts(Set.of(invoiceProduct));
                product.setInvoiceProducts(Set.of(invoiceProduct));
                invoice.setTotal(product.calculateProductTotal()[2]);
                invoice.setSubtotal(product.calculateProductTotal()[0]);
                invoice.setVATtotal(product.calculateProductTotal()[1]);
                productRepository.save(product);
                invoiceRepository.save(invoice);
                invoiceProductRepository.save(invoiceProduct);
                if (product.getQtyAvailable() > 1) {
                    product.setQtyAvailable(product.getQtyAvailable() - 1);
                    productRepository.save(product);
                } else {
                    product.setQtyAvailable(0);
                    productRepository.save(product);
                    deletedProducts.add(product);
                }
            } else if (tempTotal.compareTo(BigDecimal.valueOf(500)) < 0) {
                if (product.getQtyAvailable() <= 50 && product.checkProductQtyTotalOverValue(500, product.getQtyAvailable())) {
                    int qty = product.getQtyAvailable();
                    tempTotal = tempTotal.add(calculatedPrice(product, qty).get("total"));
                    tempSubtotal = tempSubtotal.add(calculatedPrice(product, qty).get("subtotal"));
                    tempVATtotal = tempVATtotal.add(calculatedPrice(product, qty).get("VATtotal"));
                    if (tempTotal.compareTo(BigDecimal.valueOf(500)) > 0) {
                        InvoiceTotalsDTO invoiceTotalsDTO = tempTotalOver500(product, tempTotal, tempSubtotal, tempVATtotal, qty);
                        qty = invoiceTotalsDTO.getQty();
                        tempTotal = invoiceTotalsDTO.getTotal();
                        tempSubtotal = invoiceTotalsDTO.getSubtotal();
                        tempVATtotal = invoiceTotalsDTO.getVATtotal();
                    }
                    tempProducts.put(product, qty);
                    product.setQtyAvailable(product.getQtyAvailable() - qty);
                    if (qty == product.getQtyAvailable()) {
                        deletedProducts.add(product);
                    }
                    productRepository.save(product);
                } else if (product.checkProductQtyTotalOverValue(500, 1)) {
                    int qty = 50;
                    tempTotal = tempTotal.add(calculatedPrice(product, qty).get("total"));
                    tempSubtotal = tempSubtotal.add(calculatedPrice(product, qty).get("subtotal"));
                    tempVATtotal = tempVATtotal.add(calculatedPrice(product, qty).get("VATtotal"));
                    if (tempTotal.compareTo(BigDecimal.valueOf(500)) > 0) {
                        InvoiceTotalsDTO invoiceTotalsDTO = tempTotalOver500(product, tempTotal, tempSubtotal, tempVATtotal, qty);
                        qty = invoiceTotalsDTO.getQty();
                        tempTotal = invoiceTotalsDTO.getTotal();
                        tempSubtotal = invoiceTotalsDTO.getSubtotal();
                        tempVATtotal = invoiceTotalsDTO.getVATtotal();
                    }
                    tempProducts.put(product, qty);
                    product.setQtyAvailable(product.getQtyAvailable() - qty);
                    productRepository.save(product);
                }
            }
        }
        orderedProducts.removeAll(deletedProducts);
        if (tempTotal.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setTotal(tempTotal);
        invoice.setSubtotal(tempSubtotal);
        invoice.setVATtotal(tempVATtotal);
        invoiceRepository.save(invoice);
        for (Map.Entry<Product, Integer> entry : tempProducts.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }
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
            generateInvoices(orderedProducts, invoices, order);
        }
    }

    public InvoiceTotalsDTO tempTotalOver500(Product product, BigDecimal tempTotal, BigDecimal tempSubtotal, BigDecimal tempVATtotal, int qty) {
        BigDecimal[] productTotals = product.calculateProductTotal();
        tempTotal = tempTotal.subtract(productTotals[2]);
        tempSubtotal = tempSubtotal.subtract(productTotals[0]);
        tempVATtotal = tempVATtotal.subtract(productTotals[1]);
        qty -= 1;
        if (tempTotal.compareTo(BigDecimal.valueOf(500)) > 0) {
            return tempTotalOver500(product, tempTotal, tempSubtotal, tempVATtotal, qty);
        } else {
            return new InvoiceTotalsDTO(qty, tempTotal, tempSubtotal, tempVATtotal);
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
}

