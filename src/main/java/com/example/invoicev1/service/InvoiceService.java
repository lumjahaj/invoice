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
        Map<Product, Integer> tempProducts = new HashMap<>();
        Map<Product, Integer> deletedProducts = new HashMap<>();
        List<Invoice> invoices = new ArrayList<>();
        generateInvoices(orderedProducts, tempProducts, deletedProducts, invoices);
    }

    public void generateInvoices(List<Product> orderedProducts, Map<Product, Integer> tempProducts, Map<Product, Integer> deletedProducts, List<Invoice> invoices) {
        for (Product product : orderedProducts) {
            if (product.getPrice().compareTo(BigDecimal.valueOf(500)) > 0) {
                Invoice invoice = new Invoice();
                if (product.getQtyAvailable() > 1) {
                    InvoiceProduct invoiceProduct = new InvoiceProduct(null, invoice, product, 1);
                    invoice.setInvoiceProducts(Set.of(invoiceProduct));
                    product.setInvoiceProducts(Set.of(invoiceProduct));
                    product.setQtyAvailable(product.getQtyAvailable() - 1);

                    productRepository.save(product);
                    invoiceRepository.save(invoice);
                    invoiceProductRepository.save(invoiceProduct);
                }
                if (product.getQtyAvailable() == 1) {
                    InvoiceProduct invoiceProduct = new InvoiceProduct(null, invoice, product, 1);
                    invoice.setInvoiceProducts(Set.of(invoiceProduct));
                    product.setInvoiceProducts(Set.of(invoiceProduct));
                    product.setQtyAvailable(0);

                    invoiceRepository.save(invoice);
                    invoiceProductRepository.save(invoiceProduct);
                    orderedProducts.remove(product);
                }
            }
            if (tempProducts.isEmpty()) {
                if (product.getQtyAvailable() > 50 && product.checkProductQtyTotalOverValue(500, 50)) {
                    tempProducts.put(product, 50);
                    deletedProducts.put(product, 50);
                    product.setQtyAvailable(product.getQtyAvailable() - 50);
                    productRepository.save(product);
                }
                if (product.getQtyAvailable() <= 50 && product.getQtyAvailable() > 0 && product.checkProductQtyTotalOverValue(500, product.getQtyAvailable())) {
                    tempProducts.put(product, product.getQtyAvailable());
                    deletedProducts.put(product, product.getQtyAvailable());
                    product.setQtyAvailable(0);
                    productRepository.save(product);
                }
            }
            if (!tempProducts.isEmpty() && !tempProducts.containsKey(product) && checkTotalTempProductsOverValue(tempProducts, 400)) {
                if (product.getQtyAvailable() > 50 && product.checkProductQtyTotalOverValue(100, 50)) {
                    tempProducts.put(product, 50);
                    deletedProducts.put(product, 50);
                    product.setQtyAvailable(product.getQtyAvailable() - 50);
                    productRepository.save(product);
                }
                if (product.getQtyAvailable() <= 50 && product.getQtyAvailable() > 0 && product.checkProductQtyTotalOverValue(100, product.getQtyAvailable())) {
                    tempProducts.put(product, product.getQtyAvailable());
                    deletedProducts.put(product, product.getQtyAvailable());
                    product.setQtyAvailable(0);
                    productRepository.save(product);
                }
            }
           if (!checkTotalTempProductsOverValue(tempProducts, 400)){
                Invoice invoice = new Invoice();
                for (Map.Entry<Product, Integer> entry : tempProducts.entrySet()) {
                    InvoiceProduct invoiceProduct = new InvoiceProduct(null, invoice, entry.getKey(), entry.getValue());
                    invoice.getInvoiceProducts().add(invoiceProduct);
                    entry.getKey().getInvoiceProducts().add(invoiceProduct);

                    productRepository.save(product);
                    invoiceRepository.save(invoice);
                    invoiceProductRepository.save(invoiceProduct);
                }
                tempProducts.clear();
                orderedProducts.removeIf(p -> p.getQtyAvailable() == 0);
            }
        }
        while (!orderedProducts.isEmpty()) {
            generateInvoices(orderedProducts, tempProducts, deletedProducts, invoices);
        }
    }

    public boolean checkTotalTempProductsOverValue(Map<Product, Integer> products, int value) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            BigDecimal[] totalsForOneProduct = entry.getKey().calculateProductTotal();
            BigDecimal totalForProductQty = totalsForOneProduct[2].multiply(BigDecimal.valueOf(entry.getValue()));
            total = total.add(totalForProductQty);
        }
        return !(total.compareTo(BigDecimal.valueOf(value)) > 0);
    }

    public Invoice updateInvoice(Invoice invoice) {
        Invoice existingInvoice = invoiceRepository.findById(invoice.getId()).orElse(null);
        existingInvoice.setSubtotal(invoice.getSubtotal());
        return invoiceRepository.save(existingInvoice);
    }
}

