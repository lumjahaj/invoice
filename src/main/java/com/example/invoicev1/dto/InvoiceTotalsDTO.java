package com.example.invoicev1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class InvoiceTotalsDTO {
    private Integer qty;
    private BigDecimal total;
    private BigDecimal subtotal;
    private BigDecimal VATtotal;


}

