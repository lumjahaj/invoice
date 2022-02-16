package com.example.invoicev1.controller;


import com.example.invoicev1.dto.ViewInvoiceDTO;
import com.example.invoicev1.dto.ViewProductDTO;
import com.example.invoicev1.entity.Invoice;
import com.example.invoicev1.entity.InvoiceProduct;
import com.example.invoicev1.entity.Order;
import com.example.invoicev1.entity.Product;
import com.example.invoicev1.repository.InvoiceRepository;
import com.example.invoicev1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import java.util.Set;


@RestController
@RequestMapping(path = "/api")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping(path = "/getInvoices/{id}")
    public List<ViewInvoiceDTO> getInvoicesByOrderId(@PathVariable Long id) {
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
                viewProductDTO.setSubtotal(product.getSubtotal());
                viewProductDTO.setTotal(product.getTotal());
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
}
