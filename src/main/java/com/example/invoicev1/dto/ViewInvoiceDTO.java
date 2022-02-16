package com.example.invoicev1.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ViewInvoiceDTO {
    private Long id;
    private BigDecimal subtotal;
    private BigDecimal VATtotal;
    private BigDecimal total;
    private List<ViewProductDTO> products;
}
