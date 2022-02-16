package com.example.invoicev1.controller;


import com.example.invoicev1.dto.ViewInvoiceDTO;
import com.example.invoicev1.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/api")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PutMapping(path = "/generateInvoices/{id}")
    public List<ViewInvoiceDTO> generateInvoicesByOrderId(@PathVariable Long id) {
        return invoiceService.saveInvoicesByOrderId(id);
    }

    @GetMapping(path = "/getInvoices/{id}")
    public List<ViewInvoiceDTO> getInvoicesByOrderId(@PathVariable Long id) {
        return invoiceService.getInvoicesByOrderId(id);
    }
}
