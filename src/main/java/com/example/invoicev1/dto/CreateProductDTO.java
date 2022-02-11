package com.example.invoicev1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CreateProductDTO {
    private String description;
    private BigDecimal price;
    private Integer VAT;
    private BigDecimal discount;
    @JsonProperty("qtyavailable")
    private Integer QtyAvailable;
}
