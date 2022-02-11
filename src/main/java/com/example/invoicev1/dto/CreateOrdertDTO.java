package com.example.invoicev1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class CreateOrdertDTO {
    @JsonProperty("products")
    private List<CreateProductDTO> products;
}
