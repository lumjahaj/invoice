package com.example.invoicev1.service;

import com.example.invoicev1.dto.InvoiceTotalsDTO;
import com.example.invoicev1.dto.TempProductDTO;
import com.example.invoicev1.dto.ViewInvoiceDTO;
import com.example.invoicev1.dto.ViewProductDTO;
import com.example.invoicev1.entity.Invoice;
import com.example.invoicev1.entity.InvoiceProduct;
import com.example.invoicev1.entity.Order;
import com.example.invoicev1.entity.Product;
import com.example.invoicev1.repository.InvoiceProductRepository;
import com.example.invoicev1.repository.InvoiceRepository;
import com.example.invoicev1.repository.OrderRepository;
import com.example.invoicev1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InvoiceProductRepository invoiceProductRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, ProductRepository productRepository, OrderRepository orderRepository, InvoiceProductRepository invoiceProductRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.invoiceProductRepository = invoiceProductRepository;
    }

    public List<ViewInvoiceDTO> getInvoicesByOrderId(Long id) {
        List<Invoice> invoices = invoiceRepository.findInvoicesByOrder_Id(id);
        List<ViewInvoiceDTO> invoiceDTOS = new ArrayList<>();
        for (Invoice invoice:invoices) {
            Set<InvoiceProduct> invoiceProductSet = invoice.getInvoiceProducts();
            List<ViewProductDTO> productDTOS = new ArrayList<>();
            for (InvoiceProduct ip: invoiceProductSet) {
                Product product = ip.getProduct();
                ViewProductDTO viewProductDTO = new ViewProductDTO();
                viewProductDTO.setId(product.getId());
                viewProductDTO.setDescription(product.getDescription());
                viewProductDTO.setQty(ip.getQty());
                viewProductDTO.setPrice(product.getPrice());
                viewProductDTO.setDiscount(product.getDiscount());
                viewProductDTO.setVAT(product.getVAT());
                viewProductDTO.setVATtotal(ip.getVATtotal());
                viewProductDTO.setSubtotal(ip.getSubtotal());
                viewProductDTO.setTotal(ip.getTotal());
                productDTOS.add(viewProductDTO);
            }
            ViewInvoiceDTO viewInvoiceDTO = new ViewInvoiceDTO();
            viewInvoiceDTO.setId(invoice.getId());
            viewInvoiceDTO.setSubtotal(invoice.getSubtotal());
            viewInvoiceDTO.setVATtotal(invoice.getVATtotal());
            viewInvoiceDTO.setTotal(invoice.getTotal());
            viewInvoiceDTO.setProducts(productDTOS);
            invoiceDTOS.add(viewInvoiceDTO);
        }
        return invoiceDTOS;
    }

    public List<ViewInvoiceDTO> saveInvoicesByOrderId(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new IllegalStateException("order with this id does not exist!");
        }
        Order order = optionalOrder.get();
        List<Product> orderedProducts = order.getOrderProducts();
        List<Invoice> invoices = new ArrayList<>();
        generateInvoices(orderedProducts, invoices, order);
        List<ViewInvoiceDTO> invoiceDTOS = new ArrayList<>();
        for (Invoice invoice: invoices) {
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
        return invoiceDTOS;
    }

    public void generateInvoices(List<Product> orderedProducts, List<Invoice> invoices, Order order) {
        List<TempProductDTO> tempProducts = new ArrayList<>();
        List<Product> deletedProducts = new ArrayList<>();
        BigDecimal tempTotal = BigDecimal.ZERO;
        BigDecimal tempSubtotal = BigDecimal.ZERO;
        BigDecimal tempVATtotal = BigDecimal.ZERO;
        for (Product product : orderedProducts) {
            if (product.getPrice().compareTo(BigDecimal.valueOf(500)) > 0 && tempProducts.isEmpty()) {
                Invoice invoice = new Invoice();
                invoice.setOrder(order);
                invoiceRepository.save(invoice);
                BigDecimal total, subtotal, VATtotal;
                subtotal = product.calculateProductTotal()[0];
                VATtotal = product.calculateProductTotal()[1];
                total = product.calculateProductTotal()[2];
                InvoiceProduct invoiceProduct = new InvoiceProduct(null, invoice, product, 1, subtotal, VATtotal, total);
                invoice.setInvoiceProducts(Set.of(invoiceProduct));
                product.setInvoiceProducts(Set.of(invoiceProduct));
                invoice.setTotal(total);
                invoice.setSubtotal(subtotal);
                invoice.setVATtotal(VATtotal);
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
                    TempProductDTO tempProductDTO = new TempProductDTO();
                    tempProductDTO.setProduct(product);
                    int qty = product.getQtyAvailable();
                    BigDecimal total, subtotal, VATtotal;
                    subtotal = calculatedPrice(product, qty).get("subtotal");
                    VATtotal = calculatedPrice(product, qty).get("VATtotal");
                    total = calculatedPrice(product, qty).get("total");
                    tempTotal = tempTotal.add(total);
                    tempSubtotal = tempSubtotal.add(subtotal);
                    tempVATtotal = tempVATtotal.add(VATtotal);
                    if (tempTotal.compareTo(BigDecimal.valueOf(500)) > 0) {
                        InvoiceTotalsDTO invoiceTotalsDTO = tempTotalOver500(product, tempTotal, tempSubtotal, tempVATtotal, qty);
                        qty = invoiceTotalsDTO.getQty();
                        tempTotal = invoiceTotalsDTO.getTotal();
                        tempSubtotal = invoiceTotalsDTO.getSubtotal();
                        tempVATtotal = invoiceTotalsDTO.getVATtotal();
                    }
                    subtotal = calculatedPrice(product, qty).get("subtotal");
                    VATtotal = calculatedPrice(product, qty).get("VATtotal");
                    total = calculatedPrice(product, qty).get("total");
                    tempProductDTO.setQty(qty);
                    tempProductDTO.setSubtotal(subtotal);
                    tempProductDTO.setVATtotal(VATtotal);
                    tempProductDTO.setTotal(total);
                    tempProducts.add(tempProductDTO);
                    product.setQtyAvailable(product.getQtyAvailable() - qty);
                    if (qty == product.getQtyAvailable()) {
                        deletedProducts.add(product);
                    }
                    productRepository.save(product);
                } else if (product.checkProductQtyTotalOverValue(500, 1)) {
                    TempProductDTO tempProductDTO = new TempProductDTO();
                    tempProductDTO.setProduct(product);
                    int qty = 50;
                    BigDecimal total, subtotal, VATtotal;
                    subtotal = calculatedPrice(product, qty).get("subtotal");
                    VATtotal = calculatedPrice(product, qty).get("VATtotal");
                    total = calculatedPrice(product, qty).get("total");
                    tempTotal = tempTotal.add(total);
                    tempSubtotal = tempSubtotal.add(subtotal);
                    tempVATtotal = tempVATtotal.add(VATtotal);
                    if (tempTotal.compareTo(BigDecimal.valueOf(500)) > 0) {
                        InvoiceTotalsDTO invoiceTotalsDTO = tempTotalOver500(product, tempTotal, tempSubtotal, tempVATtotal, qty);
                        qty = invoiceTotalsDTO.getQty();
                        tempTotal = invoiceTotalsDTO.getTotal();
                        tempSubtotal = invoiceTotalsDTO.getSubtotal();
                        tempVATtotal = invoiceTotalsDTO.getVATtotal();
                    }
                    subtotal = calculatedPrice(product, qty).get("subtotal");
                    VATtotal = calculatedPrice(product, qty).get("VATtotal");
                    total = calculatedPrice(product, qty).get("total");
                    tempProductDTO.setQty(qty);
                    tempProductDTO.setSubtotal(subtotal);
                    tempProductDTO.setVATtotal(VATtotal);
                    tempProductDTO.setTotal(total);
                    tempProducts.add(tempProductDTO);
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
        for (TempProductDTO tempProductDTO : tempProducts) {
            if (tempProductDTO.getQty() == 0) {
                continue;
            }
            InvoiceProduct invoiceProduct = new InvoiceProduct(
                    null,
                    invoice,
                    tempProductDTO.getProduct(),
                    tempProductDTO.getQty(),
                    tempProductDTO.getSubtotal(),
                    tempProductDTO.getVATtotal(),
                    tempProductDTO.getTotal());
            invoice.getInvoiceProducts().add(invoiceProduct);
            invoiceRepository.save(invoice);
            tempProductDTO.getProduct().getInvoiceProducts().add(invoiceProduct);
            productRepository.save(tempProductDTO.getProduct());
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

