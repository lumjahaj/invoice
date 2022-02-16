package com.example.invoicev1.dto;

import com.example.invoicev1.entity.Product;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TempProductDTO {
    private Product product;
    private Integer qty;
    private BigDecimal total;
    private BigDecimal subtotal;
    private BigDecimal VATtotal;
}
