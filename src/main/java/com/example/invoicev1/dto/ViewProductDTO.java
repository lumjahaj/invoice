package com.example.invoicev1.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ViewProductDTO {
    private Long id;
    private String description;
    private BigDecimal price;
    private Integer VAT;
    private BigDecimal discount;
    private Integer qty;
    private BigDecimal subtotal;
    private BigDecimal total;
}
