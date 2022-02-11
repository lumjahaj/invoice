package com.example.invoicev1.controller;

import com.example.invoicev1.dto.CreateProductDTO;
import com.example.invoicev1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(path = "/addProducts")
    public ResponseEntity<String> addProducts(@RequestBody List<CreateProductDTO> products) {
        productService.saveProducts(products);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
